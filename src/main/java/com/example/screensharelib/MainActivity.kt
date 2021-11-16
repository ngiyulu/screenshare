package com.example.screensharelib

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import net.glance.android.*

class MainActivity : AppCompatActivity(), VisitorListener, DefaultUI.DefaultUIListener {

    val GLANCE_GROUP_ID = 21192
    private var GLANCE_SESSION_KEY = "12345"
    val VISITOR_ID : String? = null // "123four"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the Visitor with the group ID provided by Glance
        if (VISITOR_ID != null) {
            Visitor.init(this, GLANCE_GROUP_ID, "", "", "", "", VISITOR_ID)
        } else {
            Visitor.init(this, GLANCE_GROUP_ID, "", "", "", "", "")
        }        // Listen for DefaultUIListener events. See listener methods below (optional)
        Visitor.addMaskedViewId(R.id.textView2)
        // Make sure the Visitor is listening for events on this Activity
        Visitor.addListener(this)
        Visitor.addDefaultUIListener(this)
        val startSessionBtn = findViewById<Button>(R.id.startSession)
        startSessionBtn.setOnClickListener {
            startSession()
        }
        val endSessionBtn = findViewById<Button>(R.id.endSession)
        endSessionBtn.visibility = View.GONE
        endSessionBtn.setOnClickListener {
            endSession()
        }
        val openBrowserBtn = findViewById<Button>(R.id.openBrowser)
        openBrowserBtn.setOnClickListener {
            openBrowser()
        }
    }

    fun start2WayVideoFront(v: View) {
        Visitor.startVideoCall(GLANCE_SESSION_KEY, "front", "https://ww2.glance.net/", false);
    }

    fun start2WayVideoBack(v: View) {
        Visitor.startVideoCall(GLANCE_SESSION_KEY, "back", 0, 0, 0, 0, "https://ww2.glance.net/");
    }

    override fun onResume() {
        super.onResume()
        updateView()
    }

    override fun onDestroy() {
        super.onDestroy()
        Visitor.removeMaskedViewId(R.id.textView2)

        // Make sure to remove the listener before this Activity is destroyed
        Visitor.removeListener(this)
    }

    private fun updateView() {
        runOnUiThread {
            val startSessionButton = findViewById<Button> (R.id.startSession);
            val endSessionButton = findViewById<Button> (R.id.endSession);
            startSessionButton.visibility = View.VISIBLE;
            endSessionButton.visibility = View.VISIBLE;
            endSessionButton.isEnabled = true
            findViewById<TextView> (R.id.textView2).text = GLANCE_SESSION_KEY;
        }
    }

    private fun presenceConnected() {
        runOnUiThread {
            findViewById<TextView> (R.id.presenceIndicator).text = "Presence Connected"
            findViewById<Button> (R.id.startSession).visibility = View.GONE
        }
    }
    private fun startSession() {
        val button = findViewById<Button>(R.id.startSession)
        button.isEnabled = false
        Visitor.startSession()
    }

    private fun endSession() {
        val button = findViewById<Button>(R.id.endSession)
        button.isEnabled = false
        Visitor.endSession()
    }

    private fun openBrowser() {
        val context = this
        val intent = Intent(context, WebView::class.java)
        startActivity(intent)

        if (VISITOR_ID != null) {
            val url = HashMap<String, String>()
            url["url"] = "webview https://www.glance.net"
            PresenceVisitor.presence(url)
        }
    }
    override fun defaultUIVoiceDidAuthenticate(p0: String?) {

    }

    override fun defaultUIDidHangUp() {
        Log.e("event", "defaultUIDidHangUp")
    }

    override fun defaultUIDidMute() {

    }

    override fun defaultUIDidUnmute() {

    }

    override fun defaultUIDidTurnSpeakerphoneOn() {

    }

    override fun defaultUIDidTurnSpeakerphoneOff() {

    }

    override fun defaultUIDidClose() {
        Log.e("event", "defaultUIDidClose")
    }

    override fun glanceDefaultUIVoiceAuthenticationRequired() {

    }

    override fun glanceDefaultUIVoiceAuthenticationFailed() {

    }

    override fun glanceDefaultUIDialogAccepted() {
        Log.e("event", "glanceDefaultUIDialogAccepted")
    }

    override fun glanceDefaultUIDialogCancelled() {
        Log.e("event", "glanceDefaultUIDialogCancelled")

    }

    override fun glanceVideoDefaultUIDialogAccepted() {
        Log.d("VideoDemo_kotlin", "glanceVideoDefaultUIDialogAccepted")
    }

    override fun glanceVideoDefaultUIDialogCancelled() {
        Log.d("VideoDemo_kotlin", "glanceVideoDefaultUIDialogCancelled")
    }

    override fun onGlanceVisitorEvent(event: Event) {
        Log.e("event", event.code.toString())
        if (event.code == EventCode.EventConnectedToSession) {
            // Show any UI to indicate the session has started
            // If not using a known session key, you can get the random key here (sessionKey)
            // to display to the user to read to the agent
//            sessionKey = event.GetValue("sessionkey")
//            Log.e("sessionKey", sessionKey?: "empty")
            updateView()
            hideButtons()
        } else if (event.code == EventCode.EventSessionEnded) {
            updateView()
            showButtons()
        } else if (event.type == EventType.EventWarning ||
            event.type == EventType.EventError ||
            event.type == EventType.EventAssertFail) {
            // Best practice is to log code and message of all events of these types
            Log.d("EVENT_TYPE", event.type.toString() + " " + event.messageString)
        }
        if (VISITOR_ID != null) {
            when (event.code) {
                EventCode.EventVisitorInitialized -> {
                    Log.e("event", "EventVisitorInitialized")
                    PresenceVisitor.connect()
                }
                EventCode.EventPresenceConnected -> {
                    Log.e("event", "EventPresenceConnected")
                    presenceConnected()
                }
                EventCode.EventPresenceConnectFail -> {
                    Log.e("event", "EventPresenceConnectFail")
                    Log.d("EVENT_TYPE", "Presence connection failed (will retry): " + event.messageString)
                }
                EventCode.EventPresenceShowTerms -> {
                    // You only need to handle this event if you are providing a custom UI for terms and/or confirmation
                    Log.d("EVENT_TYPE", "Agent signalled ShowTerms")
                }
                EventCode.EventPresenceBlur -> {
                    // This event notifies the app that the visitor is now using another app or website
                }
            }
        }
    }

    private fun hideButtons() {
        runOnUiThread {
            findViewById<Button>(R.id.startSessionFront).visibility = View.GONE
            findViewById<Button>(R.id.startSessionBack).visibility = View.GONE
            findViewById<Button>(R.id.endSession).visibility = View.VISIBLE
        }

    }

    private fun showButtons() {
        runOnUiThread {
            findViewById<Button>(R.id.startSession).isEnabled = true
            findViewById<Button>(R.id.startSessionFront).visibility = View.VISIBLE
            findViewById<Button>(R.id.startSessionBack).visibility = View.VISIBLE
            findViewById<Button>(R.id.endSession).visibility = View.GONE
        }

    }
}
