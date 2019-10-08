package us.mzhang.minesweeper.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.google.android.material.snackbar.Snackbar
import us.mzhang.minesweeper.R
import us.mzhang.minesweeper.model.Field
import us.mzhang.minesweeper.model.MinesweeperModel

class MinesweeperView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    var paintBackground: Paint = Paint()
    var paintLine: Paint = Paint()
    var paintText: Paint = Paint()

    var bitmapBomb: Bitmap = BitmapFactory.decodeResource(context?.resources, R.drawable.bomb)
    var bitmapFlag: Bitmap = BitmapFactory.decodeResource(context?.resources, R.drawable.flag)

    init {
        paintBackground.color = Color.GRAY
        paintBackground.style = Paint.Style.FILL

        paintLine.color = Color.WHITE
        paintLine.style = Paint.Style.STROKE
        paintLine.strokeWidth = 7f

        paintText.color = Color.GREEN
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paintBackground)

        drawBoard(canvas)
        drawFieldState(canvas)

    }

    private fun drawBoard(canvas: Canvas?) {
        // border
        canvas?.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paintLine)

        val dim = MinesweeperModel.dimension
        // set sizes of icons
        bitmapBomb = Bitmap.createScaledBitmap(bitmapBomb, width / dim, height / dim, false)
        bitmapFlag = Bitmap.createScaledBitmap(bitmapFlag, width / dim, height / dim, false)

        for (i in 0 until dim) {
            canvas?.drawLine(
                0f, ((i + 1) * height / dim).toFloat(), width.toFloat(),
                ((i + 1) * height / dim).toFloat(), paintLine
            )

            canvas?.drawLine(
                ((i + 1) * width / dim).toFloat(), 0f, ((i + 1) * width / dim).toFloat(),
                height.toFloat(), paintLine
            )
        }
    }

    private fun isGameOver(): Boolean {
        var count = 0
        for (i in 0 until MinesweeperModel.dimension) {
            for (j in 0 until MinesweeperModel.dimension) {
                val field = MinesweeperModel.getFieldContent(i, j)
                if (bombExplodedCheck(field)) return true
                if (flaggedEmptyCheck(field)) return true
                count += flaggedBombCheck(field)
            }
        }
        return allBombsFlagged(count)
    }

    private fun bombExplodedCheck(field: Field) : Boolean {
        if (field.wasClicked && field.type == MinesweeperModel.BOMB) {
            Snackbar.make(this, context.getString(R.string.BombExplode), Snackbar.LENGTH_LONG).show()
            return true
        }
        return false
    }

    private fun flaggedEmptyCheck(field: Field) : Boolean {
        if (field.isFlagged && field.type == MinesweeperModel.EMPTY) {
            Snackbar.make(this, context.getString(R.string.FlaggedEmpty), Snackbar.LENGTH_LONG).show()
            return true
        }
        return false
    }

    private fun flaggedBombCheck(field: Field) : Int {
        if (field.isFlagged && field.type == MinesweeperModel.BOMB) {
            return 1
        }
        return 0
    }

    private fun allBombsFlagged(count: Int) : Boolean {
        if (count == MinesweeperModel.bombIndices.distinct().count()) {
            Snackbar.make(this, context.getString(R.string.Win), Snackbar.LENGTH_LONG).show()
            return true
        }
        return false
    }

    private fun displayAll() {
        val dim = MinesweeperModel.dimension
        for (i in 0 until dim) {
            for (j in 0 until dim) {
                MinesweeperModel.setFieldWasClicked(i,j)
            }
        }
    }

    private fun drawFieldState(canvas: Canvas?) {
        val dim = MinesweeperModel.dimension
        paintText.textSize = (height / dim / 2).toFloat()

        for (i in 0 until dim) {
            for (j in 0 until dim) {
                val field = MinesweeperModel.getFieldContent(i, j)
                if (field.type == MinesweeperModel.BOMB && field.wasClicked) {
                    canvas?.drawBitmap(bitmapBomb, (i * height / dim).toFloat(),
                        (j * width / dim).toFloat(), null
                    )
                } else if (field.type == MinesweeperModel.EMPTY && field.wasClicked) {
                    canvas?.drawText(field.minesAround.toString(), ((i + .35) * height / dim).toFloat(),
                        ((j + .65) * width / dim).toFloat(),
                        paintText
                    )
                } else if (field.isFlagged) {
                    canvas?.drawBitmap(bitmapFlag, (i * height / dim).toFloat(), (j * width / dim).toFloat(),
                        null
                    )
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        val dim = MinesweeperModel.dimension

        if (isGameOver()) {
            displayAll()
            invalidate()
            return true
        }

        if (event?.action == MotionEvent.ACTION_DOWN) {
            val tX =
                if (event.x.toInt() / (width / dim) >= MinesweeperModel.dimension) MinesweeperModel.dimension - 1
                else event.x.toInt() / (width / dim)
            val tY =
                if (event.y.toInt() / (height / dim) >= MinesweeperModel.dimension) MinesweeperModel.dimension - 1
                else event.y.toInt() / (height / dim)

            updateModelOnAction(tX, tY)

            invalidate()

        }

        return true
    }

    private fun updateModelOnAction(tX : Int, tY: Int) {
        if (MinesweeperModel.mode) {
            MinesweeperModel.setFieldWasClicked(tX, tY)
            if (MinesweeperModel.getFieldContent(tX, tY).type == MinesweeperModel.EMPTY
                && MinesweeperModel.getFieldContent(tX, tY).minesAround == 0
            ) {
                MinesweeperModel.revealEmpties(tX + 1, tY)
                MinesweeperModel.revealEmpties(tX, tY + 1)
                MinesweeperModel.revealEmpties(tX, tY - 1)
                MinesweeperModel.revealEmpties(tX - 1, tY)
            }
        } else {
            MinesweeperModel.setFieldIsFlagged(tX, tY)
        }
    }


}