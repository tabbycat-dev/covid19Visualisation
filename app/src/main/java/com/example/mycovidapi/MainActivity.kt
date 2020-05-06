@file:Suppress("UNREACHABLE_CODE")

package com.example.mycovidapi

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.gson.GsonBuilder
import okhttp3.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.IOException

open class DemoBase
class MainActivity : AppCompatActivity(), OnChartValueSelectedListener {
    //private val  dataVal : ArrayList<BarEntry> = ArrayList<BarEntry>()

    lateinit var chart : BarChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_main)
        getAsyncFacts("China")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.australia -> {
                print("change to Australia")
                getAsyncFacts(item.title.toString())
            }
            R.id.us -> {
                print("change to US")
                getAsyncFacts(item.title.toString())
            }
            R.id.china -> { print("change to China")
                getAsyncFacts(item.title.toString())}

            R.id.vietnam -> { print(" change to Vietnam")
                getAsyncFacts(item.title.toString())}
            else -> { // Note the block
                print("china")
            }
        }
        return true
            }

    fun getAsyncFacts(country : String) {
        doAsync {
            println("Attempting to Fetch JSON")
            val url = "https://pomber.github.io/covid19/timeseries.json"
            //val url = "https://api.letsbuildthatapp.com/youtube/home_feed"
            val request = Request.Builder().url(url).build()
            val  dataVal : ArrayList<BarEntry> = ArrayList<BarEntry>()

            var client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()
                    // TODO #1 { ERROR: Socket error->Make sure to refesh emulator}
                    println(body?.length)
                    val gson = GsonBuilder().create()

                    val lists = gson.fromJson(body, ListOfCase::class.java)
                    //dataVal = ArrayList<BarEntry>()
                    var valX: Float = 0.0f
                    var selectedCountryList: Array<DateSeries>
                    when (country) {
                        "Australia" -> selectedCountryList = lists.Australia
                        "US" -> selectedCountryList = lists.US
                        "China" -> selectedCountryList = lists.China
                        "Vietnam" -> selectedCountryList = lists.Vietnam
                        else -> selectedCountryList = lists.China
                    }
                    for (i in selectedCountryList) {
                        println(i.confirmed.toFloat())
                        val valY: Float = i.confirmed.toFloat()
                        println(valX)
                        val value = BarEntry(valX, valY)
                        valX += 1.0f
                        dataVal.add(value)
                    }
                    //TODO now please visualize data of confirmed in CHina
                    if( dataVal.isNotEmpty()) {
                        //val barDataSet = BarDataSet(values, "")
                        val barDataSet = BarDataSet(dataVal, "")

                        val dataSets = ArrayList<IBarDataSet>()
                        dataSets.add(barDataSet)

                        val barData = BarData(dataSets)
                        barData.setValueTextSize(10f)

                        uiThread {
                            print("Starting to visualize data")
                            if (barData.dataSetCount == 0) {
                                print("bAR data is empty")
                            } else visualizeData2(barData)

                        }
                    }else{
                        print("Val is empty")
                    }

                }

                override fun onFailure(call: Call, e: IOException) {
                    println("Failure to Fetch JSON")
                    println(e.localizedMessage)

                }
            })








        }
    }

    fun fetchJSON( country : String){
        println("Attempting to Fetch JSON")
        val url = "https://pomber.github.io/covid19/timeseries.json"
        //val url = "https://api.letsbuildthatapp.com/youtube/home_feed"
        val request = Request.Builder().url(url).build()
        val  dataVal : ArrayList<BarEntry> = ArrayList<BarEntry>()

        var client = OkHttpClient()
        client.newCall(request).enqueue(object:  Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                // TODO #1 { ERROR: Socket error->Make sure to refesh emulator}
                println(body?.length)
                val gson = GsonBuilder().create()

                val lists = gson.fromJson(body, ListOfCase::class.java)
                //dataVal = ArrayList<BarEntry>()
                var valX : Float  =  0.0f
                var selectedCountryList :Array<DateSeries>
                when (country){
                     "Australia" ->selectedCountryList = lists.Australia
                    "US" -> selectedCountryList = lists.US
                    "China" -> selectedCountryList = lists.China
                    "Vietnam" -> selectedCountryList = lists.Vietnam
                    else -> selectedCountryList = lists.China
                }
                for ( i in lists.China ){
                    println( i.confirmed.toFloat())
                    val valY : Float = i.confirmed.toFloat()
                    println(valX)
                    val value = BarEntry( valX , valY)
                    valX += 1.0f
                    dataVal.add(value)
                }
                //visualizeData2(dataVal)
                //TODO now please visualize data of confirmed in CHina


            }
            override fun onFailure(call: Call, e: IOException) {
                println("Failure to Fetch JSON")
                println(e.localizedMessage)

            }
        })

    }
    fun visualizeData2(barData : BarData){

        if(barData.dataSetCount == 0){
            print("2-Bar Data  is empty")
        }else {
            chart = findViewById(R.id.chart1)
            chart.data = barData
            chart.invalidate()
            print("Completing visualize")

        }
    }

    override fun onNothingSelected() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
class ListOfCase (val China : Array<DateSeries> , val US : Array<DateSeries> , val Vietnam : Array<DateSeries> ,val Australia : Array<DateSeries>   ) {
}

class DateSeries (val date : String, val confirmed : Int, val deaths: Int, val recovered: Int )

