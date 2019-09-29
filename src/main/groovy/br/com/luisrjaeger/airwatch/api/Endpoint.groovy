package br.com.luisrjaeger.airwatch.api

class Endpoint {

    static final String API = "api/"
    static final String MAM = "${API}mam/"

    static final String APPS = "${MAM}apps/"
    static final String BLOBS = "${MAM}blobs/"

    static final String INTERNAL = "${APPS}internal/"
    static final String BEGIN_INSTALL = "${INTERNAL}begininstall"
    static final String INSTALL_UNINSTALL = "${INTERNAL}%d/%s"
    static final String DEVICES_ID = "${INTERNAL}%d/devices?status=%s&pagesize=10000"

    static final String APPS_SEARCH = "${APPS}search?bundleid=%s&pagesize=10000"

    static final String UPLOAD_BLOB = "${BLOBS}uploadblob?filename=%s&organizationgroupid=%d"

}