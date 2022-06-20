# Base this image on core-image-base
DESCRIPTION = "auto eth image"

COMPATIBLE_MACHINE = "^rpi$"

IMAGE_FEATURES += "splash ssh-server-openssh"

#     kernel-modules
IMAGE_INSTALL = "\
    packagegroup-core-boot \
    packagegroup-core-full-cmdline \
    linuxptp \
    ppstool \
    openssl-bin \
    openssl \
    libpcap \
    tcpdump \
    igb-avb \
    pciutils \
    boost \
    vsomeip \
    libavtp \
    strongswan \
    python3 \
    python3-pip \
    lan867x \
    usbutils \
    libusb \
    capicxx-someip-runtime \
    ${CORE_IMAGE_EXTRA_INSTALL} \
    "

inherit core-image


