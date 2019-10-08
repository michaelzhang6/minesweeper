package us.mzhang.minesweeper.model

import kotlin.random.Random

object MinesweeperModel {

    const val EMPTY: Short = 0
    const val BOMB: Short = 1

    const val TRY: Boolean = true
    var mode = TRY

    const val dimension: Int = 8
    const val numBombs: Int = (dimension*dimension) / 8

    var bombIndices: Array<Pair<Int, Int>> = Array(numBombs) {
        Pair(Random.nextInt(0, dimension), Random.nextInt(0, dimension))
    }

    private var model = Array(dimension) { i ->
        Array(dimension) { j ->
            if (bombIndices.contains(Pair(i, j))) {
                Field(BOMB, 0, false, false)
            } else {
                Field(EMPTY, 0, false, false)
            }
        }
    }

    fun updateMinesAround() {
        for (i in 0 until dimension) {
            for (j in 0 until dimension) {
                if (getFieldContent(i, j).type != BOMB) {
                    if (i-1 >= 0 && j-1 >= 0) model[i][j].minesAround += model[i-1][j-1].type
                    if (i+1 < dimension && j-1 >= 0) model[i][j].minesAround += model[i+1][j-1].type
                    if (j-1 >= 0) model[i][j].minesAround += model[i][j-1].type
                    if (i+1 < dimension && j+1 < dimension) model[i][j].minesAround += model[i+1][j+1].type
                    if (i-1 >= 0 && j+1 < dimension) model[i][j].minesAround += model[i-1][j+1].type
                    if (j+1 < dimension) model[i][j].minesAround += model[i][j+1].type
                    if (i+1 < dimension) model[i][j].minesAround += model[i+1][j].type
                    if (i-1 >= 0) model[i][j].minesAround += model[i-1][j].type
                }
            }
        }
    }

    fun getFieldContent(i: Int, j: Int): Field {
        return model[i][j]
    }

    fun setFieldWasClicked(i: Int, j: Int) {
        model[i][j].wasClicked = true
    }

    fun setFieldIsFlagged(i: Int, j: Int) {
        model[i][j].isFlagged = true
    }

    fun revealEmpties(i : Int, j : Int) {
        if (i < 0 || i >= dimension || j < 0 || j >= dimension || model[i][j].type == BOMB ||
            model[i][j].minesAround != 0 || model[i][j].wasClicked) return

        model[i][j].wasClicked = true

        revealEmpties(i+1, j)
        revealEmpties(i, j+1)
        revealEmpties(i, j-1)
        revealEmpties(i-1, j)
    }

    fun resetGame() {
        bombIndices = Array(numBombs) {
            Pair(Random.nextInt(0, dimension), Random.nextInt(0, dimension))
        }

        model = Array(dimension) { i ->
            Array(dimension) { j ->
                if (bombIndices.contains(Pair(i, j))) {
                    Field(BOMB, 0, false, false)
                } else {
                    Field(EMPTY, 0, false, false)
                }
            }
        }
    }


}