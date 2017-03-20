require lcdproc5.inc
SRC_URI[md5sum] = "2f8e064233ef1d03e98c0b426595750d"
SRC_URI[sha256sum] = "843007d377adc856529ed0c7c42c9a7563043f06b1b73add0372bba3a3029804"

PACKAGECONFIG[g15] = ",,libg15 g15daemon libg15render,"
PACKAGECONFIG[hid] = "--enable-libhid,--disable-libhid,libhid"
PACKAGECONFIG[png] = "--enable-libpng,--disable-libpng,libpng"

LCD_DRIVERS_append = "${@bb.utils.contains('PACKAGECONFIG', 'g15', '', ',!g15', d)}"

do_install_append () {
    # binaries
    install -D -m 0755 clients/lcdvc/lcdvc ${D}${sbindir}/lcdvc

    # configuration files
    install -D -m 0644 ${S}/clients/lcdvc/lcdvc.conf ${D}${sysconfdir}/lcdvc.conf
}

PACKAGES =+ "lcdvc"
CONFFILES_lcdvc = "${sysconfdir}/lcdvc.conf"
FILES_lcdvc = "${sysconfdir}/lcdvc.conf ${sbindir}/lcdvc"

