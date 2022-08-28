package com.bikash.simpleapicall

import android.app.Dialog
import android.app.ProgressDialog
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.Socket
import java.net.SocketTimeoutException
import java.net.URL

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle??) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        CALLAPILoginAsyncTask("bikash","12345").execute()
    }

    private inner class CALLAPILoginAsyncTask(val username: String,val password: String): AsyncTask<Any, Void, String>(){

        private lateinit var customProgressDialog: Dialog

        override fun onPreExecute() {
            super.onPreExecute()
            showProgressDialog()
        }

        override fun doInBackground(vararg p0: Any?): String {
            var result:String
            var connection:HttpURLConnection? = null
            try {
                var url = URL("http://www.mocky.io/v2/5e3826143100006a00d37ffa")
                connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.doOutput = true

                connection.instanceFollowRedirects = false

                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type","application/json")
                connection.setRequestProperty("charset","utf-8")
                connection.setRequestProperty("Accept","application/json")

                connection.useCaches = false

                val writeDataOutputStream = DataOutputStream(connection.outputStream)
                val jsonRequest = JSONObject()
                jsonRequest.put("username", username)
                jsonRequest.put("password", password)

                writeDataOutputStream.writeBytes(jsonRequest.toString())
                writeDataOutputStream.flush()
                writeDataOutputStream.close()

                val httpResult : Int = connection.responseCode
                if (httpResult == HttpURLConnection.HTTP_OK){
                    val inputStream = connection.inputStream

                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val stringBuilder = StringBuilder()
                    var line: String?
                    try {
                        while (reader.readLine().also { line = it} != null){
                            stringBuilder.append(line + "\n")
                        }
                    }catch (e: IOException){
                        e.printStackTrace()
                    }finally {
                        try {
                            inputStream.close()
                        }catch (e: IOException){
                            e.printStackTrace()
                        }
                    }
                    result = stringBuilder.toString()

                }else{
                    result = connection.responseMessage
                }
            }catch (e:SocketTimeoutException){
                result = "Connection TimeOut"

            }catch (e:Exception){
                result = "Error:" + e.message
            }finally {
                connection?.disconnect()
            }
            return result
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            cancelProgressDialog()
            if (result != null) {
                Log.i("JSON RESPONSE RESULT", result)
            }


            //ACCESSING JSON OBJECT USING GSON(Java library that can be used to convert Java Objects into their JSON)

            val responseData = Gson().fromJson(result,ResponseData::class.java)
            Log.i("Message",responseData.message)
            Log.i("Id","${responseData.id}")
            Log.i("name",responseData.name)


            Log.i("email",responseData.email)
            Log.i("mobile","${responseData.mobile}")

            Log.i("is profile created","${responseData.profile_details.is_profile_completed}")
            Log.i("ratings","${responseData.profile_details.rating}")

            for (item in responseData.data_list.indices){
                Log.i("Values $item","${responseData.data_list[item]}")
                Log.i("ID $item","${responseData.data_list[item].id}")
                Log.i("Value $item", responseData.data_list[item].value)
            }







            // USED ONLY FOR JSON OBJECT ONLY

           /* val jsonObject = JSONObject(result)
            val message = jsonObject.optString("messasge")
            Log.i("Message",message)
            val userId = jsonObject.optInt("id")
            Log.i("id","$userId")
            val name= jsonObject.optString("name")
            Log.i("Name",name)


            //to call a json object having nested json object
            val profileDetailsObject = jsonObject.optJSONObject("profile_details")
            val isProfileCompleted = profileDetailsObject?.optBoolean("is_profile_completed")
            Log.i("Is profile completed","$isProfileCompleted")


            //to call a json object having nested json object array
            val dataListArray = jsonObject.optJSONArray("data_list")
            Log.i("Data list size", "${dataListArray?.length()}")
            for (item in 0 until dataListArray.length()){
                Log.i("Value $item","${dataListArray[item]}")
                //nested json object inside array
                val dataItemObject: JSONObject = dataListArray[item] as JSONObject

                val id = dataItemObject.optInt("id")
                Log.i("ID","$id")
                val value = dataItemObject.optString("value")
                Log.i("Value",value)
            }

            */





        }

        private fun showProgressDialog(){
            customProgressDialog = Dialog(this@MainActivity)
            customProgressDialog.setContentView(R.layout.dialog_custom_progress)
            customProgressDialog.show()
        }

        private fun cancelProgressDialog(){
            customProgressDialog.dismiss()
        }

    }



}