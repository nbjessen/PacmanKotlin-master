package org.pondar.pacmankotlin

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity(), OnClickListener {

    //reference to the game class.
    private var game: Game? = null

    private var myTimer: Timer = Timer()
    private var gameTimer: Timer = Timer()
    private var counter : Int = 60

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_main)
        game = Game(this,pointsView)

        game?.setGameView(gameView)
        gameView.setGame(game)
        game?.newGame()

        startButton.setOnClickListener(this)
        stopButton.setOnClickListener(this)
        nextLevel.setOnClickListener(this)


        //make a new timer
        game?.running = true //should the game be running?
        //We will call the timer 5 times each second
        myTimer.schedule(object : TimerTask() {
            override fun run() {
                timerMethod()
            }

        }, 0, 17) //0 indicates we start now, 200
        //is the number of miliseconds between each call

        //timer
        gameTimer.schedule(object : TimerTask() {
            override fun run() {
                timerValue()
            }
        },0,1000)

        moveRight.setOnClickListener {
            game?.direction = 3}
        moveLeft.setOnClickListener {
            game?.direction = 4}
        moveUp.setOnClickListener {
            game?.direction = 1}
        moveDown.setOnClickListener {
            game?.direction = 2
        }
    }

    override fun onStop() {
        super.onStop()
        //just to make sure if the app is killed, that we stop the timer.
        myTimer.cancel()
        gameTimer.cancel()
    }
    private fun timerValue(){
        this.runOnUiThread(timerSecond)
    }

    private  val timerSecond = Runnable {
        textView.text = "${getString(R.string.timer_value)} $counter secs"
        if (game?.running!!){
            if (game?.direction == 1) {
                counter--
                textView.text = "${getString(R.string.timer_value)} $counter secs"
            }
            if (game?.direction == 2){
                counter--
                textView.text = "${getString(R.string.timer_value)} $counter secs"
            }
            if (game?.direction == 3){
                counter--
                textView.text = "${getString(R.string.timer_value)} $counter secs"
            }
            if (game?.direction == 4){
                counter--
                textView.text = "${getString(R.string.timer_value)} $counter secs"
            }
        }
    }


    private fun timerMethod() {
        //This method is called directly by the timer
        //and runs in the same thread as the timer.

        //we could do updates here TO GAME LOGIC,
        // but not updates TO ACTUAL UI

        // gameView.move(20)  // BIG NO NO TO DO THIS - WILL CRASH ON OLDER DEVICES!!!!


        //We call the method that will work with the UI
        //through the runOnUiThread method.
        this.runOnUiThread(timerTick)

    }

    private val timerTick = Runnable {
        //This method runs in the same thread as the UI.
        // so we can draw
        if (game?.running!!) {
            //update the counter - notice this is NOT seconds in this example
            //you need TWO counters - one for the timer count down that will
            // run every second and one for the pacman which need to run
            //faster than every second

            if (game?.direction==1)
            {
                game?.movePacmanUp(5)
                game?.moveGhostRight(2)
            }
            else if (game?.direction==2)
            {
                game?.movePacmanDown(5)
                game?.moveGhostLeft(2)
            }
            else if (game?.direction==3)
            {
                game?.movePacmanRight(5)
                game?.moveGhostUp(2)
            }
            else if (game?.direction==4)
            {
                game?.movePacmanLeft(5)
                game?.moveGhostDown(2)
            }
            if (counter <= 0){
                game?.direction = 0
                game?.running = false
            }
            if(game?.isGameWon() == true){
                val toast = Toast.makeText(this, "Level Completed!", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }
            if (game?.running == false){
                val toast = Toast.makeText(this, "Game Over", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }
            //move the pacman - you
            //should call a method on your game class to move
            //the pacman instead of this
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    @SuppressLint("StringFormatInvalid")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        if (id == R.id.action_settings) {
            Toast.makeText(this, "settings clicked", Toast.LENGTH_SHORT).show()
            return true
        } else if (id == R.id.action_newGame) {
            Toast.makeText(this, "New Game clicked", Toast.LENGTH_SHORT).show()
            game?.newGame()
            counter = 60
            textView.text = getString(R.string.timer_value,counter)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            if (v.id == R.id.startButton) {
                game?.running = true
            } else if (v.id == R.id.stopButton) {
                game?.running = false
            }
            //Level progression
            else if(v.id == R.id.nextLevel) {
                //can click next level if game is won
                if (game?.isGameWon() == true){
                    //Spawn more enemies
                    game?.initializeEnimies()
                    //clear coin array
                    game?.coins?.clear()
                    //reset pacman
                    game?.pacx = 450
                    game?.pacy = 600
                    //spawn new coins
                    game?.initializeGoldcoins()
                    //reset direction / stop movement
                    game?.direction = 0
                    //set running to true
                    game?.running = true
                    //position each ghost a random place
                    for (ghost in game?.ghosts!!){
                        ghost.enemyx = Random().nextInt(900)
                        ghost.enemyy = Random().nextInt(400)
                    }
                    //add 30 seconds
                    counter += 30
                    //redraw screen
                    gameView?.invalidate()
                }
            }
        }
    }
}
