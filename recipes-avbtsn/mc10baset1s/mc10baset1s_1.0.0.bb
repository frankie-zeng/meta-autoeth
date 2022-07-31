SUMMARY = "Frankie Zeng 10Base T1S board control script."
HOMEPAGE = "https://github.com/frankie-zeng/mc10baset1s"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "python3-pyusb"

SRCREV_machine = "cd348d3d5f5372314ea5315fdbf487a896c25d02"

SRC_URI = "git://github.com/frankie-zeng/mc10baset1s.git;name=machine;branch=master;protocol=https \
		   file://GPLv2.patch \
           "
S = "${WORKDIR}/git"


FILES:${PN} += "${bindir}/mc10baset1s.py"



do_install() {
	install -d ${D}${bindir}
	install -c -m 0644 ${S}/mc10baset1s.py ${D}${bindir}/mc10baset1s.py
}


