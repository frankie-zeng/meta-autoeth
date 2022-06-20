SUMMARY = "PPS Tool"
SECTION = "base"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI = "file://main.c \
           file://GPLv2.patch \
"

S = "${WORKDIR}"

do_compile() {
    ${CC} ${LDFLAGS} main.c -o pps
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 pps ${D}${bindir}
}