#!/bin/bash
#
# /*------------------------------------------------------------------------------------------------*/
# /* (c) 2021 Microchip Technology Inc. and its subsidiaries.                                       */
# /*                                                                                                */
# /* You may use this software and any derivatives exclusively with Microchip products.             */
# /*                                                                                                */
# /* THIS SOFTWARE IS SUPPLIED BY MICROCHIP "AS IS".  NO WARRANTIES, WHETHER EXPRESS, IMPLIED OR    */
# /* STATUTORY, APPLY TO THIS SOFTWARE, INCLUDING ANY IMPLIED WARRANTIES OF NON-INFRINGEMENT,       */
# /* MERCHANTABILITY, AND FITNESS FOR A PARTICULAR PURPOSE, OR ITS INTERACTION WITH MICROCHIP       */
# /* PRODUCTS, COMBINATION WITH ANY OTHER PRODUCTS, OR USE IN ANY APPLICATION.                      */
# /*                                                                                                */
# /* IN NO EVENT WILL MICROCHIP BE LIABLE FOR ANY INDIRECT, SPECIAL, PUNITIVE, INCIDENTAL OR        */
# /* CONSEQUENTIAL LOSS, DAMAGE, COST OR EXPENSE OF ANY KIND WHATSOEVER RELATED TO THE SOFTWARE,    */
# /* HOWEVER CAUSED, EVEN IF MICROCHIP HAS BEEN ADVISED OF THE POSSIBILITY OR THE DAMAGES ARE       */
# /* FORESEEABLE. TO THE FULLEST EXTENT ALLOWED BY LAW, MICROCHIP'S TOTAL LIABILITY ON ALL CLAIMS   */
# /* IN ANY WAY RELATED TO THIS SOFTWARE WILL NOT EXCEED THE AMOUNT OF FEES, IF ANY, THAT YOU HAVE  */
# /* PAID DIRECTLY TO MICROCHIP FOR THIS SOFTWARE.                                                  */
# /*                                                                                                */
# /* MICROCHIP PROVIDES THIS SOFTWARE CONDITIONALLY UPON YOUR ACCEPTANCE OF THESE TERMS.            */
# /*------------------------------------------------------------------------------------------------*/
#
# Some general hints:
# 
# How to check Kernel version
# uname -r
#
# Example how to get Kernel Headers
# sudo apt-get install raspberrypi-kernel-headers
# 
# Example how to install GCC
# sudo apt install build-essential
#
# t1s.sh
# Roland Trissl (RTR)
# For support related to this code contact http://www.microchip.com/support

# Cleanup
if [ $# -eq 5 ]; then
  sudo rm *.mod
  sudo rm *.mod.c
  sudo rm *.mod.o
  sudo rm *.o
  sudo rm *.ko
  cd phy-driver
  cd src
  sudo rm *.o
  cd ..
  cd ..
  cd smsc95xx-drv
  sudo rm *.o
  cd ..
  sudo rm modules.order
  sudo rm Module.symvers
  exit 0
fi

# Check, if config file exists
if [ ! -f "/lib/firmware/t1scfg/plca-8-$1.bin" ]; 
then
  echo -e "\033[31mThe required configuration file does not exist."
  echo -e "\033[31mPlease contact http://www.microchip.com/support"
else
  if [ $# -eq 1 ] 
  then
    echo -e "\033[36mConfigure 10BASE-T1S adapter as node $1:\033[0m"
    # Copy required configuration file
    sudo cp /lib/firmware/t1scfg/plca-8-$1.bin /lib/firmware/lan867x_config.bin
    # Remove old stuff if there
    echo -e "\033[36mRemoved drivers:\033[0m"
	
	mac=$( lsmod | grep -o "smsc95xx_t1s" )
    if [ "$mac" != "smsc95xx_t1" ]; then
      echo -
    else
      sudo rmmod smsc95xx_t1s
	  sleep 1
    fi
	
	phy=$( lsmod | grep -o "lan867x_phy" )
    if [ "$phy" != "lan867x_phy" ]; then
      echo -
    else
      sudo rmmod lan867x_phy
	  sleep 1
    fi	
    
    # Add the modules
    sudo insmod lan867x_phy.ko
    sleep 1
    sudo insmod smsc95xx_t1s.ko
    sleep 1
  fi

  if [ $# -eq 0 ] 
  then
    echo -e "\033[36mUsage: $0 0                 - Configures adapter as node 0 (PLCA coordinator) and sets up eth1.\033[0m"
    echo -e "\033[36m       $0 1                 - Configures adapter as node 1 and sets up eth1.\033[0m"
    echo -e "\033[36m       $0 7                 - Configures adapter as node 7 and sets up eth1.\033[0m"
    echo -e "\033[36m       $0 1 x               - Keeps driver configuration, just sets up eth1.\033[0m"
  else
    # Print info
    echo -e "\033[36mPresent drivers:\033[0m"
    lsmod | grep -iE "mchp|smsc|lan867x"
	
    # Check if adapter is present
    lsmod | grep 'lan867x_phy' >/dev/null
    if [ $? -ne 0  ]
    then
      echo -e "\033[31mMCHP 10BASE-T1S adapter not found.\033[0m"
    else
      echo -e "\033[32mMCHP 10BASE-T1S adapter found.\033[0m"
      ipadr=`expr 10 + $1`
      sudo ip addr flush dev eth1
      sudo ip addr change 192.168.0.$ipadr/24 dev eth1
      sleep 1
      ip -4 addr | grep "eth1"
    fi
    echo -e "\033[36mDone.\033[0m"
  fi
fi
