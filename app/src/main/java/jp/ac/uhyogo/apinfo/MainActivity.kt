package jp.ac.uhyogo.apinfo

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
// import androidx.lifecycle.lifecycleScope
// import androidx.work.WorkManager
// import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    // private val manager = WorkManager.getInstance()
    var apInfo = ArrayList<APInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // AP情報の取得間隔 [ms]
        //　val interval = 1000

        // wi-fiマネージャーの起動
        val wifiManager = this.getSystemService(Context.WIFI_SERVICE) as WifiManager
        // wi-fi off のとき，このアプリにおいてonにする
        if (!wifiManager.isWifiEnabled) {
            Toast.makeText(this, "Wifi is disable ... We need to enable it.", Toast.LENGTH_SHORT).show()
            wifiManager.isWifiEnabled
        }

        // permissionの設定
        val REQUEST_PERMISSIONS_ID = 127            // リクエスト識別用のユニークな値(数値はなんでもいい)
        val reqPermissions = ArrayList<String>()    // リクエスト用
        reqPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION)    // 必要なpermissionをリクエスト に追加
        // パーミションのリクエスト
        ActivityCompat.requestPermissions(this, reqPermissions.toTypedArray(), REQUEST_PERMISSIONS_ID)

        // AP情報の取得開始
        apScanTask(wifiManager)
    }

    private fun apScanTask(wifiManager: WifiManager){
        // wi-fiスキャンのローンチ
        // lifecycleScope.launch {
        apScanBackgroundTask(wifiManager)
        // }
    }

    private fun apScanBackgroundTask(wifiManager: WifiManager) {
        val wifiScanReceiver = object : BroadcastReceiver() {
            // AP情報を取得したときの動作
            override fun onReceive(context: Context, intent: Intent) {
                val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)

                if (success) {
                    scanSuccess(context, wifiManager)   // スキャン成功時
                } else {
                    scanFailure()   // スキャン失敗時
                }
            }
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        applicationContext.registerReceiver(wifiScanReceiver, intentFilter)

        val success = wifiManager.startScan()
        if (!success) {
            // scanの開始自体を失敗したとき
            Log.i("Scan", "Failure")
        }
    }

    // スキャン成功時
    private fun scanSuccess(context: Context, wifiManager: WifiManager) {
        Log.i("Scan", "Success")
        val results = wifiManager.scanResults
        displayScanResults(context, results)
    }

    // スキャン失敗時
    private fun scanFailure() {
        Log.i("Scan", "Failure")
    }

    // スキャン結果を反映
    private fun displayScanResults(context: Context, responses:MutableList<ScanResult>?){
        Log.i("Responses", responses.toString())

        apInfo = arrayListOf()

        for (res in responses!!){
            val apData = APInfo(
                res.SSID,
                res.BSSID,
                res.level,
                res.frequency
            )

            apInfo.add(apData)
        }

        // 取得時刻の表示
        val localDateTime = LocalDateTime.now()
        val tvTimestamp: TextView = findViewById(R.id.timestamp)
        val dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS")
        tvTimestamp.text = localDateTime.format(dtf)
        Log.i("datetime", localDateTime.format(dtf))

        // ListView
        val lv:ListView = findViewById(R.id.lv)
        val adapter = APInfoAdapter(context, apInfo)
        lv.adapter = adapter
    }

}