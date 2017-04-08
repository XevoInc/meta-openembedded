# This recipe is modified from the recipe originally found in the Open-Switch
# repository:
#
# https://github.com/open-switch/ops-build
# yocto/openswitch/meta-foss-openswitch/meta-oe/recipes-support/open-vm-tools/open-vm-tools_10.0.5.bb
# Commit 9008de2d8e100f3f868c66765742bca9fa98f3f9
#

DECRIPTION = "open-vm-tools"
SECTION = "vmware-tools"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=5804fe91d3294da4ac47c02b454bbc8a"

PR = "r5"

SRC_URI = "git://github.com/vmware/open-vm-tools.git;protocol=https \
           file://tools.conf \
           file://vmtoolsd.service \
           file://0001-Fix-kernel-detection.patch \
           file://0002-Fix-build-failure-with-GCC-6.patch \
           file://0003-nicInfo-fix-header-include-order.patch \
           file://0004-configure.ac-don-t-use-dnet-config.patch \
           "

SRCREV = "stable-10.0.5"

S = "${WORKDIR}/git/open-vm-tools"

DEPENDS = "virtual/kernel glib-2.0 util-linux libdnet procps fuse"
RDEPENDS_${PN} = "util-linux libdnet fuse"

inherit autotools systemd

SYSTEMD_SERVICE_${PN} = "vmtoolsd.service"

EXTRA_OECONF = "--without-icu --disable-multimon --disable-docs --disable-tests \
                --without-gtk2 --without-gtkmm --without-xerces --without-pam \
                --disable-grabbitmqproxy --disable-vgauth --disable-deploypkg \
                --without-root-privileges --without-kernel-modules"

EXTRA_OECONF += "${@bb.utils.contains('DISTRO_FEATURES', 'x11', '', '--without-x', d)}"

EXTRA_OEMAKE = "KERNEL_RELEASE=${KERNEL_VERSION}"


CFLAGS += '-Wno-error=deprecated-declarations'

FILES_${PN} += "/usr/lib/open-vm-tools/plugins/vmsvc/lib*.so \
		/usr/lib/open-vm-tools/plugins/common/lib*.so \
    ${sysconfdir}/vmware-tools/tools.conf"
FILES_${PN}-locale += "/usr/share/open-vm-tools/messages"
FILES_${PN}-dev += "/usr/lib/open-vm-tools/plugins/common/lib*.la"
FILES_${PN}-dbg += "/usr/lib/open-vm-tools/plugins/common/.debug \
		    /usr/lib/open-vm-tools/plugins/vmsvc/.debug"

CONFFILES_${PN} += "${sysconfdir}/vmware-tools/tools.conf"

do_install_append() {
    ln -sf /usr/sbin/mount.vmhgfs ${D}/sbin/mount.vmhgfs
    install -d ${D}${systemd_unitdir}/system ${D}${sysconfdir}/vmware-tools
    install -m 644 ${WORKDIR}/*.service ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/tools.conf ${D}${sysconfdir}/vmware-tools/tools.conf
}

do_configure_prepend() {
    export CUSTOM_PROCPS_NAME=procps
    export CUSTOM_PROCPS_LIBS=-L${STAGING_LIBDIR}/libprocps.so
    export CUSTOM_DNET_NAME=dnet
    export CUSTOM_DNET_LIBS=-L${STAGING_LIBDIR}/libdnet.so
}
