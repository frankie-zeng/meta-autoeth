EXTRA_OECONF:append = " \
       --enable-remote \
"
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append=" file://rpcapd.service"

FILES:${PN} += "${systemd_system_unitdir}/multi-user.target.wants/rpcapd.service ${systemd_system_unitdir}/rpcapd.service"


do_install () {

       oe_runmake 'DESTDIR=${D}' install
       install -d ${D}${systemd_system_unitdir}
	install -c -m 0644 ${WORKDIR}/rpcapd.service ${D}${systemd_system_unitdir}
       
       install -d ${D}${systemd_system_unitdir}/multi-user.target.wants
       ln -sf ../rpcapd.service ${D}${systemd_system_unitdir}/multi-user.target.wants/rpcapd.service
}