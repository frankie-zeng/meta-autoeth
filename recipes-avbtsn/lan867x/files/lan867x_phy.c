/*
 * drivers/net/phy/lan867x_phy.c
 *
 * Driver for Microchip 10BASE-T1S LAN867X PHY
 *
 * Author: Parthiban Veerasooran
 * Author: Jan Huber
 *
 * Copyright (c) 2018 Microchip Technology Inc.
 *
 * This program is free software; you can redistribute  it and/or modify it
 * under  the terms of  the GNU General  Public License as published by the
 * Free Software Foundation;  either version 2 of the  License, or (at your
 * option) any later version.
 */

#include <linux/kernel.h>
#include <linux/module.h>
#include <linux/phy.h>
#include <linux/firmware.h>
#include <linux/delay.h>

static const struct firmware *fw_p;

MODULE_DESCRIPTION("Microchip 10BASE-T1S lan867x Phy driver");
MODULE_AUTHOR("Parthiban Veerasooran <Parthiban.Veerasooran@microchip.com>");
MODULE_AUTHOR("Jan Huber <Jan.Huber@microchip.com>");
MODULE_LICENSE("GPL");
MODULE_VERSION("0.5");

static int mk_u32(const u8 *ptr, u32 *value)
{
	*value = ptr[0] << 24 | ptr[1] << 16 | ptr[2] << 8 | ptr[3];
	return 4;
}

static int mk_u16(const u8 *ptr, u16 *value)
{
	*value = ptr[0] << 8 | ptr[1];
	return 2;
}

static void write_reg(struct phy_device *phydev, u32 addr, u16 val)
{
	if (addr > 0xFF)
		phy_write_mmd(phydev, addr >> 16, addr & 0xFFFF, val);
	else
		phy_write(phydev, addr, val);
}

static int read_reg(struct phy_device *phydev, u32 addr)
{
	if (addr > 0xFF)
		return phy_read_mmd(phydev, addr >> 16, addr & 0xFFFF);

	return phy_read(phydev, addr);
}

enum { LAN867X_CFG_TYPE = 1 };

static const int opcode_lengts[] = { 6, 8, 10, 10, 2, 12, 12 };

static bool configure_lan867x(struct phy_device *phydev, const u8 *ptr,
			     const u8 *end);

static int config_init(struct phy_device *phydev)
{
	const u8 *ptr;
	const u8 *end;
	u32 type, length, offset;
	int ret = request_firmware_direct(&fw_p, "lan867x_config.bin", NULL);

	if (ret)
		return ret;

	phydev->speed = SPEED_10;
	phydev->autoneg = DUPLEX_HALF;

	ptr = fw_p->data;
	end = fw_p->data + fw_p->size;

	while (ptr < end) {
		offset = ptr - fw_p->data;
		if (ptr + 8 > end) {
			pr_err("bad TLV format, config offset: %d\n", offset);
			goto err;
		}
		ptr += mk_u32(ptr, &type);
		ptr += mk_u32(ptr, &length);
		if (ptr + length > end) {
			pr_err("bad TLV length, config offset: %d\n", offset);
			goto err;
		}
		if (type == LAN867X_CFG_TYPE) {
			if (!configure_lan867x(phydev, ptr, ptr + length))
				goto err;
		}
		ptr += length;
	}

	release_firmware(fw_p);
	return 0;

err:
	release_firmware(fw_p);
	return -EFAULT;
}

static bool configure_lan867x(struct phy_device *phydev, const u8 *ptr,
			     const u8 *end)
{
	u32 addr;
	u16 mask, value, skip, reg, count, delay;

	while (ptr < end) {
		u8 op = *ptr++;

		if (op >= ARRAY_SIZE(opcode_lengts)) {
			pr_err("bad opcode: %u\n", op);
			goto err;
		}

		if (ptr + opcode_lengts[op] > end) {
			pr_err("bad script format\n");
			goto err;
		}

		switch (op) {
		case 0:
			ptr += mk_u32(ptr, &addr);
			ptr += mk_u16(ptr, &value);
			write_reg(phydev, addr, value);
			break;
		case 1:
			ptr += mk_u32(ptr, &addr);
			ptr += mk_u16(ptr, &mask);
			ptr += mk_u16(ptr, &value);
			reg = read_reg(phydev, addr) & ~mask;
			write_reg(phydev, addr, reg | value);
			break;
		case 2:
			ptr += mk_u32(ptr, &addr);
			ptr += mk_u16(ptr, &mask);
			ptr += mk_u16(ptr, &value);
			ptr += mk_u16(ptr, &skip);
			reg = read_reg(phydev, addr) & mask;
			if (reg == value)
				break;

			if (!skip) {
				pr_err("value (*%08x & %04x) == %04x is expected to be %04x\n",
				       addr, mask, reg, value);
				goto err;
			}
			ptr += skip;
			break;
		case 3:
			ptr += mk_u32(ptr, &addr);
			ptr += mk_u16(ptr, &mask);
			ptr += mk_u16(ptr, &value);
			ptr += mk_u16(ptr, &skip);
			reg = read_reg(phydev, addr) & mask;
			if (reg != value)
				break;

			if (!skip) {
				pr_err("value (*%08x & %04x) is not expected to be %04x\n",
				       addr, mask, reg);
				goto err;
			}
			ptr += skip;
			break;
		case 4:
			ptr += mk_u16(ptr, &skip);
			ptr += skip;
			break;
		case 5:
			ptr += mk_u32(ptr, &addr);
			ptr += mk_u16(ptr, &mask);
			ptr += mk_u16(ptr, &value);
			ptr += mk_u16(ptr, &count);
			ptr += mk_u16(ptr, &delay);
			while ((reg = read_reg(phydev, addr) & mask) != value) {
				if (!count) {
					pr_err("value (*%08x & %04x) == %04x is expected to be %04x\n",
					       addr, mask, reg, value);
					goto err;
				}
				mdelay(delay);
				count--;
			}
			break;
		case 6:
			ptr += mk_u32(ptr, &addr);
			ptr += mk_u16(ptr, &mask);
			ptr += mk_u16(ptr, &value);
			ptr += mk_u16(ptr, &count);
			ptr += mk_u16(ptr, &delay);
			while ((reg = read_reg(phydev, addr) & mask) == value) {
				if (!count) {
					pr_err("value (*%08x & %04x) is not expected to be %04x\n",
					       addr, mask, reg);
					goto err;
				}
				mdelay(delay);
				count--;
			}
			break;
		}

		if (ptr > end) {
			pr_err("bad script format\n");
			goto err;
		}
	}

	return true;

err:
	return false;
}

static struct phy_driver driver[] = {
	{
		.phy_id         = 0x0007C160,
		.name           = "Microchip 10BASE-T1S LAN867X PHY",
		.phy_id_mask    = 0x0ffffff0,
		.soft_reset     = genphy_soft_reset,
		.config_init    = config_init,
		.config_aneg    = genphy_config_aneg,
		.aneg_done      = genphy_aneg_done,
		.read_status    = genphy_read_status,
		.suspend        = genphy_suspend,
		.resume         = genphy_resume,
	}
};

module_phy_driver(driver);

static struct mdio_device_id __maybe_unused tbl[] = {
	{ 0x0007C160, 0x0ffffff0 },
	{ }
};

MODULE_DEVICE_TABLE(mdio, tbl);
