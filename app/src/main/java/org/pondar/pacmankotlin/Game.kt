package org.pondar.pacmankotlin

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import java.util.*
import javax.xml.transform.Transformer
import kotlin.collections.ArrayList


/**
 *
 * This class should contain all your game logic
 */

class Game(private var context: Context,view: TextView) {

    private var pointsView: TextView = view
    private var points : Int = 0


    //bitmaps
    var pacBitmap: Bitmap
    var goldBitmap: Bitmap
    var ghostBitmap: Bitmap
    var pacx: Int = 0
    var pacy: Int = 0
    var running = false

    var direction: Int = 0

    //the list of goldcoins
    var coins = ArrayList<GoldCoin>()

    //list of enemies
    var ghosts = ArrayList<Enemy>(1)

    //a reference to the gameview
    private var gameView: GameView? = null
    private var h: Int = 0
    private var w: Int = 0 //height and width of screen


    init {
        pacBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.pacman)
    }
    init {
        goldBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.goldcoin)
    }
    init {
        ghostBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ghost)
    }

    fun setGameView(view: GameView) {
        this.gameView = view
    }

    fun initializeGoldcoins()
    {
       for (i in 0..4){
           val coin = GoldCoin(0, 0)
            coin.coinx = Random().nextInt(950)
            coin.coiny = Random().nextInt(1000)
           coins.add(coin)
        }
    }

    fun initializeEnimies()
    {
            val ghost = Enemy(0, 0)
            ghost.enemyx = Random().nextInt(900)
            ghost.enemyy = Random().nextInt(400)
            ghosts.add(ghost)
    }

    fun newGame() {
        coins.clear()
        ghosts.clear()
        pacx = 450
        pacy = 600
        initializeGoldcoins()
        initializeEnimies()
        points = 0
        pointsView.text = "${context.resources.getString(R.string.points)} $points"
        gameView?.invalidate() //redraw screen
        direction = 0
        running = true
    }
    fun setSize(h: Int, w: Int) {
        this.h = h
        this.w = w
    }

    fun movePacmanRight(pixels: Int) {
        //still within our boundaries?
        if (pacx + pixels + pacBitmap.width < w) {
            pacx += pixels
            doCollisionCheck()
            gameView!!.invalidate()
        }
    }

    fun movePacmanLeft(pixels: Int) {
        //still within our boundaries?
        if (pacx - pixels > 0) {
            pacx -= pixels
            doCollisionCheck()
            gameView!!.invalidate()
        }
    }

    fun movePacmanUp(pixels: Int) {
        //still within our boundaries?
        if (pacy - pixels > 0) {
            pacy -= pixels
            doCollisionCheck()
            gameView!!.invalidate()
        }
    }

    fun movePacmanDown(pixels: Int) {
        //still within our boundaries?
        if (pacy + pixels + pacBitmap.height < h) {
            pacy += pixels
            doCollisionCheck()
            gameView!!.invalidate()
        }
    }

    fun moveGhostRight(pixels: Int) {
        for (ghost in ghosts){
            if (ghost.enemyx + pixels + ghostBitmap.width < w){
                ghost.enemyx += pixels
                gameView!!.invalidate()
            }
        }
    }
    fun moveGhostLeft(pixels: Int) {
        for (ghost in ghosts){
            if (ghost.enemyx - pixels > 0){
                ghost.enemyx -= pixels
                gameView!!.invalidate()
            }
        }
    }
    fun moveGhostUp(pixels: Int) {
        for (ghost in ghosts){
            if (ghost.enemyy - pixels > 0){
                ghost.enemyy -= pixels
                gameView!!.invalidate()
            }
        }
    }
    fun moveGhostDown(pixels: Int) {
        for (ghost in ghosts){
            if (ghost.enemyy + pixels + ghostBitmap.height < h){
                ghost.enemyy += pixels
                gameView!!.invalidate()
            }
        }
    }

    fun doCollisionCheck() {
        //Pacman
        val h1 = pacBitmap.height
        val w1 = pacBitmap.width
        val x1 = pacx
        val y1 = pacy
        //Collision check for ghosts
        for (ghost in ghosts){
            //Ghost
            val h3 = ghostBitmap.height
            val w3 = ghostBitmap.width
            val x3 = ghost.enemyx
            val y3 = ghost.enemyy
            //Ghost collision
            var ghostCollisionX = false
            var ghostCollisionY = false
            //Check which position is lesser on x
            if (x1 < x3){
                //check if there is gap on x axis
                if (x1 + w1 > x3){
                    ghostCollisionX = true
                }
            }
            else{
                //check if there is a gap on x axis
                if (x3 + w3 > x1){
                    ghostCollisionX = true
                }
            }

            //check which position is lesser on y
            if (y1 < y3){
                //check if there is a gab on y axis
                if (y1 + h1 > y3){
                    ghostCollisionY = true
                }
            }
            else{
                //check if there is a gab on y axis
                if (y3 + h3 > y1){
                    ghostCollisionY = true
                }
            }
            if (ghostCollisionX && ghostCollisionY){
                   if (isDead()){
                       running = false
                       direction = 0
                       Log.d("lost", "Level Lost")
                   }
                }
            }

        //Collision check for coins
        for (coin in coins){
            //Coin
            val h2 = goldBitmap.height
            val w2 = goldBitmap.width
            val x2 = coin.coinx
            val y2 = coin.coiny
            //Coin collision
            var coinCollisionX = false
            var coinCollisionY = false
            //Check which position is lesser on x
            if (x1 < x2){
                //check if there is gap on x axis
                if (x1 + w1 > x2){
                    coinCollisionX = true
                }
            }
            else{
                //check if there is a gap on x axis
                if (x2 + w2 > x1){
                    coinCollisionX = true
                }
            }

            //check which position is lesser on y
            if (y1 < y2){
                //check if there is a gab on y axis
                if (y1 + h1 > y2){
                    coinCollisionY = true
                }
            }
            else{
                //check if there is a gab on y axis
                if (y2 + h2 > y1){
                    coinCollisionY = true
                }
            }
            if (coinCollisionX && coinCollisionY){
                if (coin.taken == true){
                    continue
                }
                else{
                    //update score and check if game is won
                    coin.taken = true
                    points += 5
                    pointsView.text = "${context.resources.getString(R.string.points)} $points"
                    if (isGameWon()){
                        running = false
                        direction = 0
                        Log.d("win", "Level Completed")
                    }
                }
            }
        }
    }

    fun isGameWon(): Boolean{
        for (coin in coins){
            if (!coin.taken){
                return false
            }
        }
        return true
    }

    fun isDead(): Boolean{
        for (ghost in ghosts){
            if (!ghost.alive){
                return false
            }
        }
        return true
    }
}