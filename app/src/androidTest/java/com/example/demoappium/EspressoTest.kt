package com.example.demoappium

import android.content.Intent
import android.graphics.Bitmap
import android.text.format.DateFormat
import androidx.appcompat.app.AppCompatActivity
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException
import java.util.*


@RunWith(AndroidJUnit4::class)
@LargeTest
class EspressoTest {

    @get:Rule
    val rule = ActivityScenarioRule<MainActivity>(Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java))

    private var mFolderName: String? = null
    private var mCurrentFragment: String? = null
    private var mFileWriter: FileWriter? = null
    private var mGraph: HashMap<String, ArrayList<String>>? = null

    fun setup() {
        mFolderName = getFolderName()

        mGraph = HashMap()

        rule.scenario.onActivity {
            val folderPath = it.applicationContext.filesDir.absolutePath + "/screenshots/" + mFolderName + "/"
            val folder = File(folderPath)
            if (!folder.exists()) {
                folder.mkdirs()
            }

            val file = File(folderPath + "flow.dot")
            try {
                mFileWriter = FileWriter(file)
                mFileWriter?.let {
                    it.write("digraph MeuDiagrama {")
                    it.write(System.lineSeparator())
                }
            } catch (e: IOException) {
                println("Falha ao gerar arquivo")
            }
        }
    }

    fun getFolderName(): String {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = String.format("%02d", calendar[Calendar.MONTH] + 1)
        val day = String.format("%02d", calendar[Calendar.DAY_OF_MONTH])
        val hour = String.format("%02d", calendar[Calendar.HOUR_OF_DAY])
        val minute = String.format("%02d", calendar[Calendar.MINUTE])
        val second = String.format("%02d", calendar[Calendar.SECOND])

        return year.toString() + month + day + hour + minute + second
    }

    private fun didChangePage() {
        val graph = mGraph!!
        val currentFragment = mCurrentFragment;

        captureScreenshot()
        val nextFragment: String = getCurrentFragment() ?: "unknown";
        if (mCurrentFragment != null) {
            if (!graph.containsKey(currentFragment)) {
                val joins = ArrayList<String>()
                joins.add(nextFragment)
                graph[currentFragment!!] = joins
            } else if (!graph[currentFragment]!!.contains(nextFragment)) {
                graph[currentFragment]!!.add(nextFragment)
            }
            if (!graph.containsKey(nextFragment)) {
                val joins = ArrayList<String>()
                graph[nextFragment] = joins
            }
        }
        mCurrentFragment = nextFragment
    }

    private fun captureScreenshot() {
        val fileName = getCurrentFragment();
        val now = Date()
        DateFormat.format("yyyy-MM-dd_hh:mm:ss", now)
        try {

            val folderPath = "screenshots/$mFolderName/"

            // image naming and path  to include sd card  appending name you choose for file
            val mPath: String = "$folderPath/$fileName.png"

            // create bitmap screen capture
            getActivityInstance()?.let {
                val v1 = it.window.decorView.rootView;
                v1.setDrawingCacheEnabled(true)
                val bitmap: Bitmap = Bitmap.createBitmap(v1.drawingCache)
                v1.setDrawingCacheEnabled(false)
                val imageFile = File(mPath)
                val outputStream = FileOutputStream(imageFile)
                val quality = 100
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                outputStream.flush()
                outputStream.close()
            }
        } catch (e: Throwable) {
            // Several error may come out with file handling or DOM
            e.printStackTrace()
        }
    }

    fun getActivityInstance(): AppCompatActivity? {

        var currentActivity: AppCompatActivity? = null;

        getInstrumentation().runOnMainSync(Runnable {
            val resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED)
            if (resumedActivities.iterator().hasNext()) {
                currentActivity = resumedActivities.iterator().next() as AppCompatActivity?
            }
        })

        return currentActivity
    }

    fun getCurrentFragment(): String? {

        var fragmentName: String? = null;

        var activityInstance = getActivityInstance()
        activityInstance?.let {
            fragmentName = it.supportFragmentManager.getBackStackEntryAt(it.supportFragmentManager.backStackEntryCount - 1).name
        };

        return fragmentName;
    }

    @Test
    fun testFirstFlow() {

        setup()

        didChangePage()

        onView(withId(R.id.textview_first)).check(matches(isDisplayed()));
        onView(withId(R.id.button_first)).perform(click());
        onView(withId(R.id.textview_second)).check(matches(isDisplayed()));

        didChangePage()

        onView(withId(R.id.button_second)).perform(click());
        onView(withId(R.id.textview_third)).check(matches(isDisplayed()));

        didChangePage()

        onView(withId(R.id.button_third)).perform(click());
    }

    @Test
    fun testErrorFlow() {
        onView(withId(R.id.textview_first)).check(matches(isDisplayed()));
        onView(withId(R.id.button_first)).perform(click());

        didChangePage()

        onView(withId(R.id.textview_second)).check(matches(isDisplayed()));
        onView(withId(R.id.button_error)).perform(click());

        didChangePage();

        onView(withId(R.id.textview_error)).check(matches(isDisplayed()));
        
        finishTests()
    }
    
    fun finishTests() {
//        var errorOcurred = false
//
//        val folderPath = "screenshots/$mFolderName/"
//        val file = File(folderPath + "flow.dot")
//        try {
//            for (fragment in mGraph.keys) {
//                mFileWriter.write("node[image=\"$fragment.png\", width=2, height=2, fixedsize=true, imagescale=true, label=\"\", fillcolor=black style=filled]; $fragment;")
//                mFileWriter.write(System.lineSeparator())
//            }
//            mFileWriter.write(System.lineSeparator())
//            for ((key, value) in mGraph) {
//                for (fragment in value) {
//                    mFileWriter.write("$key->$fragment")
//                    mFileWriter.write(System.lineSeparator())
//                }
//            }
//            mFileWriter.write("}")
//        } catch (e: IOException) {
//            println("Erro ao finalizar arquivo")
//            e.printStackTrace()
//            errorOcurred = true
//        } finally {
//            try {
//                mFileWriter.close()
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//        }
//
//        val absolutePath = file.absolutePath
//        val path = absolutePath.substring(0, absolutePath.lastIndexOf(File.separator))
//        if (!errorOcurred) {
//            try {
//                Runtime.getRuntime().exec("dot -Tpng -O $path\\flow.dot", null, File(path))
//            } catch (e: IOException) {
//                println("Erro ao gerar grafo:$path")
//                e.printStackTrace()
//            }
//        }
    }
}