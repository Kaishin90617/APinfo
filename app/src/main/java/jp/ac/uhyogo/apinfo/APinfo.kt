package jp.ac.uhyogo.apinfo

data class APInfo(
    val ssid        : String,
    val address     : String?,
    val rssi        : Int?,
    val frequency   : Int?
)
