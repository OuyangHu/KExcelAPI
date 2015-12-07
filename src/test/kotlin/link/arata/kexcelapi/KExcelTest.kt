/*
 * Copyright 2015 Shinichi ARATA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package link.arata.kexcelapi

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.hamcrest.Matchers.closeTo
import org.junit.Assert.assertThat
import org.junit.BeforeClass
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.*
import org.hamcrest.Matchers.`is` as IS

class KExcelTest() {
    companion object {
        var BASE_DIR = ""

        @JvmStatic
        @BeforeClass
        fun BeforeClass() {
            val path = Paths.get(KExcelTest::class.java.getResource("book1.xlsx").toURI()).parent
            BASE_DIR = path.toString()
        }
    }

    @Test
    fun セルのラベルでの読み込みテスト() {
        KExcel.open("$BASE_DIR/book1.xlsx").use { workbook ->
            val sheet = workbook[0]

            assertThat(sheet["B2"].toStr(), IS("あいうえお"))
            assertThat(sheet["C3"].toStr(), IS("123"))
            assertThat(sheet["D4"].toStr(), IS("192.222"))
            assertThat(sheet["C2"].toStr(), IS("123"))
        }
    }

    @Test
    fun セルのインデックスでの読み込みテスト() {
        KExcel.open("$BASE_DIR/book1.xlsx").use { workbook ->
            val sheet = workbook["Sheet1"]

            assertThat(sheet[1, 1].toStr(), IS("あいうえお"))
            assertThat(sheet[2, 2].toStr(), IS("123"))
            assertThat(sheet[3, 3].toStr(), IS("192.222"))
            assertThat(sheet[2, 1].toStr(), IS("123"))
        }
    }

    @Test
    fun 同じセルに違う方法で2回アクセス() {
        KExcel.open("$BASE_DIR/book1.xlsx").use { workbook ->
            val sheet = workbook[0]

            assertThat(sheet["B2"].toStr(), IS("あいうえお"))
            assertThat(sheet[1, 1].toStr(), IS("あいうえお"))
            assertThat(sheet["G2"].toStr(), IS("123150.51"))
            assertThat(sheet[6, 1].toStr(), IS("123150.51"))
        }
    }

    @Test
    fun 文字列の取得テスト() {
        KExcel.open("$BASE_DIR/book1.xlsx").use { workbook ->
            val sheet = workbook[0]

            assertThat(sheet["B2"].toStr(), IS("あいうえお"))
            assertThat(sheet[1, 1].toStr(), IS("あいうえお"))
            assertThat(sheet["C2"].toStr(), IS("123"))
            assertThat(sheet[2, 1].toStr(), IS("123"))
            assertThat(sheet["C3"].toStr(), IS("123"))
            assertThat(sheet[2, 2].toStr(), IS("123"))
            assertThat(sheet["D2"].toStr(), IS("150.51"))
            assertThat(sheet[3, 1].toStr(), IS("150.51"))
            assertThat(sheet["D3"].toStr(), IS("105.5"))
            assertThat(sheet[3, 2].toStr(), IS("105.5"))
            assertThat(sheet["E2"].toStr(), IS("2015/12/1"))
            assertThat(sheet[4, 1].toStr(), IS("2015/12/1"))
            assertThat(sheet["F2"].toStr(), IS("true"))
            assertThat(sheet[5, 1].toStr(), IS("true"))
            assertThat(sheet["G2"].toStr(), IS("123150.51"))
            assertThat(sheet[6, 1].toStr(), IS("123150.51"))
            assertThat(sheet["G3"].toStr(), IS("369"))
            assertThat(sheet[6, 2].toStr(), IS("369"))
            assertThat(sheet["G5"].toStr(), IS("false"))
            assertThat(sheet[6, 4].toStr(), IS("false"))
            assertThat(sheet["H2"].toStr(), IS(""))
            assertThat(sheet[7, 1].toStr(), IS(""))
            assertThat(sheet["I2"].toStr(), IS(""))
            assertThat(sheet[8, 1].toStr(), IS(""))
        }
    }

    @Test
    fun 整数の取得テスト() {
        KExcel.open("$BASE_DIR/book1.xlsx").use { workbook ->
            val sheet = workbook[0]

            assertThat(sheet["C3"].toInt(), IS(123))
            assertThat(sheet[2, 2].toInt(), IS(123))
            assertThat(sheet["D3"].toInt(), IS(105))
            assertThat(sheet[3, 2].toInt(), IS(105))
            assertThat(sheet["G3"].toInt(), IS(369))
            assertThat(sheet[6, 2].toInt(), IS(369))
        }
    }

    @Test
    fun 小数の取得テスト() {
        KExcel.open("$BASE_DIR/book1.xlsx").use { workbook ->
            val sheet = workbook[0]

            assertThat(sheet["C4"].toDouble(), IS(123.0))
            assertThat(sheet[2, 3].toDouble(), IS(123.0))
            assertThat(sheet["D4"].toDouble(), closeTo(192.220, 192.224))
            assertThat(sheet[3, 3].toDouble(), closeTo(192.220, 192.224))
            assertThat(sheet["G4"].toDouble(), closeTo(64.072, 64.076))
            assertThat(sheet[6, 3].toDouble(), closeTo(64.072, 64.076))
        }
    }

    @Test
    fun 論理値の取得テスト() {
        KExcel.open("$BASE_DIR/book1.xlsx").use { workbook ->
            val sheet = workbook[0]

            assertThat(sheet["F5"].toBoolean(), IS(true))
            assertThat(sheet[5, 4].toBoolean(), IS(true))
            assertThat(sheet["G5"].toBoolean(), IS(false))
            assertThat(sheet[6, 4].toBoolean(), IS(false))
        }
    }

    @Test
    fun 日付の取得テスト() {
        KExcel.open("$BASE_DIR/book1.xlsx").use { workbook ->
            val sheet = workbook[0]

            val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm")
            assertThat(sdf.format(sheet["E6"].toDate()), IS("2015/11/30 00:00"))
            assertThat(sdf.format(sheet[4, 5].toDate()), IS("2015/11/30 00:00"))
            assertThat(sdf.format(sheet["G6"].toDate()), IS("2015/12/02 00:00"))
            assertThat(sdf.format(sheet[6, 5].toDate()), IS("2015/12/02 00:00"))

            assertThat(sdf.format(sheet["E7"].toDate()), IS("1899/12/31 10:10"))
            assertThat(sdf.format(sheet[4, 6].toDate()), IS("1899/12/31 10:10"))
            assertThat(sdf.format(sheet["G7"].toDate()), IS("1899/12/31 12:34"))
            assertThat(sdf.format(sheet[6, 6].toDate()), IS("1899/12/31 12:34"))
        }
    }


    @Test
    fun セルのラベルでの書き込みテスト() {
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val date = sdf.parse("2015/12/06 17:59:58")
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("test")

        sheet["A1"].setValue(100)
        sheet["A2"].setValue("あいうえお")
        sheet["A3"].setValue(1.05)
        sheet["A4"].setValue(date)

        assertThat(sheet["A1"].toInt(), IS(100))
        assertThat(sheet["A2"].toStr(), IS("あいうえお"))
        assertThat(sheet["A3"].toDouble(), closeTo(1.049, 1.051))
        assertThat(sdf.format(sheet["A4"].toDate()), IS("2015/12/06 17:59:58"))
    }

    @Test
    fun シートのインデックスからの書き込みテスト() {
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val date = sdf.parse("2015/12/06 17:59:58")
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("test")

        sheet[0, 0].setValue(100)
        sheet[0, 1].setValue("あいうえお")
        sheet[0, 2].setValue(1.05)
        sheet[0, 3].setValue(date)

        assertThat(sheet[0, 0].toInt(), IS(100))
        assertThat(sheet[0, 1].toStr(), IS("あいうえお"))
        assertThat(sheet[0, 2].toDouble(), closeTo(1.049, 1.051))
        assertThat(sdf.format(sheet[0, 3].toDate()), IS("2015/12/06 17:59:58"))
    }

    @Test
    fun セルのインデックスからセル名の変換テスト() {
        assertThat(KExcel.cellIndexToCellName(0, 0), IS("A1"));
        assertThat(KExcel.cellIndexToCellName(1, 0), IS("B1"));
        assertThat(KExcel.cellIndexToCellName(2, 0), IS("C1"));
        assertThat(KExcel.cellIndexToCellName(2, 1), IS("C2"));
        assertThat(KExcel.cellIndexToCellName(25, 1), IS("Z2"));
        assertThat(KExcel.cellIndexToCellName(26, 1), IS("AA2"));
        assertThat(KExcel.cellIndexToCellName(27, 1), IS("AB2"));
        assertThat(KExcel.cellIndexToCellName(255, 1), IS("IV2"));
        assertThat(KExcel.cellIndexToCellName(702, 1), IS("AAA2"));
        assertThat(KExcel.cellIndexToCellName(16383, 1), IS("XFD2"));

    }
}
