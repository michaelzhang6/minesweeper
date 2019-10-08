package us.mzhang.minesweeper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import us.mzhang.minesweeper.model.MinesweeperModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnToggle.isChecked = true
        MinesweeperModel.updateMinesAround()

        btnToggle.setOnClickListener {
            MinesweeperModel.mode = btnToggle.isChecked
        }

        btnReset.setOnClickListener {
            MinesweeperModel.resetGame()
            MinesweeperModel.updateMinesAround()
            MineView.invalidate()
        }


    }
}
