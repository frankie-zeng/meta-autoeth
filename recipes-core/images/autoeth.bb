# Base this image on core-image-base
DESCRIPTION = "auto eth image"

COMPATIBLE_MACHINE = "^rpi$"

IMAGE_FEATURES += "splash ssh-server-openssh"

#     kernel-modules
IMAGE_INSTALL = "\
    packagegroup-core-boot \
    packagegroup-core-full-cmdline \
    linuxptp \
    pps-tools \
    openssl-bin \
    openssl \
    libpcap \
    tcpdump \
    igb-avb \
    pciutils \
    boost \
    vsomeip \
    libavtp \
    ${CORE_IMAGE_EXTRA_INSTALL} \
    "

inherit core-image


