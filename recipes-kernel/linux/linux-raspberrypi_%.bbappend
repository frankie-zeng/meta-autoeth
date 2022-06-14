FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI += "file://ptp.cfg \
            file://qos.cfg \
            "
# KBUILD_DEFCONFIG_raspberrypi4-64 = "bcm2711_defconfig_ptp_file"