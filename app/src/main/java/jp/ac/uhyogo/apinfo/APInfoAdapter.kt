package jp.ac.uhyogo.apinfo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class APInfoAdapter(contex: Context, var mAPInfoList: List<APInfo>) : ArrayAdapter<APInfo>(contex, 0, mAPInfoList) {

    private val layoutInflater = contex.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // AP情報の取得
        val apInfo = mAPInfoList[position]

        // レイアウトの設定
        var view = convertView
        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.ap_info_list, parent, false)
        }

        // 各Viewの設定
        val ssid      = view?.findViewById<TextView>(R.id.ssid)
        val address   = view?.findViewById<TextView>(R.id.address)
        val rssi      = view?.findViewById<TextView>(R.id.rssi)
        val frequency = view?.findViewById<TextView>(R.id.frequency)

        ssid?.text      = apInfo.ssid
        address?.text   = apInfo.address
        rssi?.text      = apInfo.rssi.toString()
        frequency?.text = apInfo.frequency.toString()

        return view!!
    }
}