SUMMARY = "The OpenAvnu project is sponsored by the Avnu Alliance."
HOMEPAGE = "https://github.com/Avnu/OpenAvnu"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "igb-avb libpcap cpputest pciutils alsa-lib gstreamer1.0 gstreamer1.0-plugins-base"

SRCREV_machine = "ff076e83234d2207f33447b9bd6d1646d9245566"

SRC_URI = "git://github.com/Avnu/OpenAvnu.git;name=machine;branch=master;protocol=https \
           file://GPLv2.patch \
           file://cmake.patch \
           file://i210_linux.cmake \
           file://i210_linux_avdecc.cmake \
           file://0001-fix-compile-issue.patch \
           "
S = "${WORKDIR}/git"

# SRC_URI[md5sum] = "96d9ea1578162929c30b48e6813227ff"


inherit cmake


do_configure:append() {
	cmake \
	  ${OECMAKE_GENERATOR_ARGS} \
	  $oecmake_sitefile \
	  ${S}/lib/avtp_pipeline \
	  -B "${WORKDIR}/build_avtp_pipe" \
	  -DPCAPDIR="${STAGING_DIR_HOST}${prefix}" \
	  -DLIB_SUFFIX=${@d.getVar('baselib').replace('lib', '')} \
	  -DCMAKE_INSTALL_SO_NO_EXE=0 \
	  -DCMAKE_TOOLCHAIN_FILE=${WORKDIR}/i210_linux.cmake \
	  -DCMAKE_NO_SYSTEM_FROM_IMPORTED=1 \
	  -DCMAKE_EXPORT_NO_PACKAGE_REGISTRY=ON \
	  ${EXTRA_OECMAKE} \
	  -Wno-dev

	cmake \
	  ${OECMAKE_GENERATOR_ARGS} \
	  $oecmake_sitefile \
	  ${S}/lib/avtp_pipeline \
	  -B "${WORKDIR}/build_avdecc" \
	  -DPCAPDIR="${STAGING_DIR_HOST}${prefix}" \
	  -DLIB_SUFFIX=${@d.getVar('baselib').replace('lib', '')} \
	  -DCMAKE_INSTALL_SO_NO_EXE=0 \
	  -DCMAKE_TOOLCHAIN_FILE=${WORKDIR}/i210_linux_avdecc.cmake \
	  -DCMAKE_NO_SYSTEM_FROM_IMPORTED=1 \
	  -DCMAKE_EXPORT_NO_PACKAGE_REGISTRY=ON \
	  ${EXTRA_OECMAKE} \
	  -Wno-dev

}


do_compile:append() {
    cd ${S}/daemons/shaper
	oe_runmake CC="${CC}" CPPFLAGS="${CPPFLAGS}" LDFLAGS="${LDFLAGS}"

	cd ${S}/lib/common
	oe_runmake CC="${CC}" CPPFLAGS="${CPPFLAGS}" LDFLAGS="${LDFLAGS}" AVB_FEATURE_IGB=1 AVB_FEATURE_ATL=0

	${CMAKE_VERBOSE} cmake --build "${WORKDIR}/build_avtp_pipe" --target all

	${CMAKE_VERBOSE} cmake --build "${WORKDIR}/build_avdecc" --target all


}

do_install() {
	${CMAKE_VERBOSE} cmake --build "${WORKDIR}/build_avtp_pipe" --target install

	${CMAKE_VERBOSE} cmake --build "${WORKDIR}/build_avdecc" --target install
}


# MAKE_TARGETS = "kmod"

# EXTRA_OEMAKE = "--include-dir=${WORKDIR}/kmod"
