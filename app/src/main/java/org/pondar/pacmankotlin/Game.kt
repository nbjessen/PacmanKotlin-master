package org.pondar.pacmankotlin

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.os.Build
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import java.util.*


/**
 *
 * This class should contain all your game logic
 */

class Game(private var context: Context,view: TextView) {

        private var pointsView: TextView = view
        private var points : Int = 0
        //bitmap of the pacman
        var pacBitmap: Bitmap
        var goldBitmap: Bitmap
        var pacx: Int = 0
        var pacy: Int = 0


        //did we initialize the coins?
        var coinsInitialized = false


        //the list of goldcoins - initially empty
        var coins = ArrayList<GoldCoin>()

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


    fun setGameView(view: GameView) {
        this.gameView = view
    }

    //TODO initialize goldcoins also here
    fun initializeGoldcoins()
    {
       for (i in 0..4){
           val coin = GoldCoin(0, 0)
            coin.coinx = Random().nextInt(900)
            coin.coiny = Random().nextInt(1300)
           coins.add(coin)
        }

        //DO Stuff to initialize the array list with coins.
        coinsInitialized = true
        Log.d("coins", "$coins")
    }

    fun newGame() {
        coins.clear()
        pacx = 400
        pacy = 400 //just some starting coordinates - you can change this.
        //reset the points
        coinsInitialized = false
        initializeGoldcoins()
        points = 0
        pointsView.text = "${context.resources.getString(R.string.points)} $points"
        gameView?.invalidate() //redraw screen
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

    //TODO check if the pacman touches a gold coin
    //and if yes, then update the neccesseary data
    //for the gold coins and the points
    //so you need to go through the arraylist of goldcoins and
    //check each of them for a collision with the pacman

    fun doCollisionCheck() {
        for (coin in coins){
            var h1 = pacBitmap.height
            var w1 = pacBitmap.width
            var x1 = pacx
            var y1 = pacy
            var h2 = goldBitmap.height
            var w2 = goldBitmap.width
            var x2 = coin.coinx
            var y2 = coin.coiny
            var xCollision = false
            var yCollision = false
            //Check which position is lesser on x
            if (x1 < x2){
                //check if there is gap on x axis
                if (x1 + w1 > x2){
                    xCollision = true
                }
            }
            else{
                //check if there is a gap on x axis
                if (x2 + w2 > x1){
                    xCollision = true
                }
            }

            //check which position is lesser on y
            if (y1 < y2){
                //check if there is a gab on y axis
                if (y1 + h1 > y2){
                    yCollision = true
                }
            }
            else{
                //check if there is a gab on y axis
                if (y2 + h2 > y1){
                    yCollision = true
                }
            }
            if (xCollision && yCollision){
                coin.taken = true
                removeCoinAtPosition(coin.coinx, coin.coiny)
            }
        }
    }
    fun removeCoinAtPosition(x: Int, y: Int){
    


        for(coin in coins) {
            if (x==coin.coinx&&y==coin.coiny) {
                points += 5
                pointsView.text = "${context.resources.getString(R.string.points)} $points"

            }
        }
    }
}