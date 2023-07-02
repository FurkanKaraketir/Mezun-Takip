package com.karaketir.mezuntakip.services

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.karaketir.mezuntakip.R
import com.karaketir.mezuntakip.models.Person
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.util.CellUtil
import org.apache.poi.xssf.usermodel.IndexedColorMap
import org.apache.poi.xssf.usermodel.XSSFColor
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Calendar


fun openLink(link: String, context: Context) {
    val browserIntent = Intent(
        Intent.ACTION_VIEW, Uri.parse(link)
    )
    context.startActivity(browserIntent)
}

fun ImageView.glide(url: String?, placeholder: CircularProgressDrawable) {
    val options = RequestOptions().placeholder(placeholder).error(R.drawable.baseline_image_24)

    Glide.with(context.applicationContext).setDefaultRequestOptions(options).load(url).into(this)
}

fun placeHolderYap(context: Context): CircularProgressDrawable {
    return CircularProgressDrawable(context).apply {
        strokeWidth = 10f
        centerRadius = 40f
        start()
    }
}

fun createSheetHeader(cellStyle: CellStyle, sheet: Sheet) {
    //setHeaderStyle is a custom function written below to add header style

    //Create sheet first row
    val row = sheet.createRow(0)

    //Header list
    val headerList = listOf(
        "column_1",
        "column_2",
        "column_3",
        "column_4",
        "column_5",
        "column_6",
        "column_7",
        "column_8",
        "column_8",
        "column_9",

        )

    //Loop to populate each column of header row
    for ((index, value) in headerList.withIndex()) {

        val columnWidth = (15 * 500)

        sheet.setColumnWidth(index, columnWidth)

        val cell = row.createCell(index)

        cell?.setCellValue(value)

        cell.cellStyle = cellStyle
    }
}

fun getHeaderStyle(workbook: Workbook): CellStyle {

    //Cell style for header row
    val cellStyle: CellStyle = workbook.createCellStyle()

    //Apply cell color
    val colorMap: IndexedColorMap = (workbook as XSSFWorkbook).stylesSource.indexedColors
    var color = XSSFColor(IndexedColors.RED, colorMap).indexed
    cellStyle.fillForegroundColor = color
    cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND)

    //Apply font style on cell text
    val whiteFont = workbook.createFont()
    color = XSSFColor(IndexedColors.WHITE, colorMap).indexed
    whiteFont.color = color
    whiteFont.bold = true
    cellStyle.setFont(whiteFont)


    return cellStyle
}

@SuppressLint("Recycle", "Range", "SimpleDateFormat")
fun createExcel(
    context: Context, workbook: XSSFWorkbook
) {

    val time = Calendar.getInstance().time
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
    val current = "Mezun Takip Tablosu " + formatter.format(time)

    val contentUri = MediaStore.Files.getContentUri("external")

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val selection = MediaStore.MediaColumns.RELATIVE_PATH + "=?"

        val selectionArgs =
            arrayOf(Environment.DIRECTORY_DOCUMENTS + "/Mezun Takip Listesi/") //must include "/" in front and end


        val cursor: Cursor? =
            context.contentResolver.query(contentUri, null, selection, selectionArgs, null)

        var uri: Uri? = null

        if (cursor != null) {
            if (cursor.count == 0) {
                Toast.makeText(
                    context.applicationContext,
                    "Dosya Bulunamadı \"" + Environment.DIRECTORY_DOCUMENTS + "/Mezun Takip Listesi/\"",
                    Toast.LENGTH_LONG
                ).show()

                try {
                    val values = ContentValues()
                    values.put(
                        MediaStore.MediaColumns.DISPLAY_NAME, current
                    ) //file name
                    values.put(
                        MediaStore.MediaColumns.MIME_TYPE, "application/vnd.ms-excel"
                    ) //file extension, will automatically add to file
                    values.put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        Environment.DIRECTORY_DOCUMENTS + "/Mezun Takip Listesi/"
                    ) //end "/" is not mandatory
                    uri = context.contentResolver.insert(
                        MediaStore.Files.getContentUri("external"), values
                    ) //important!
                    val outputStream = context.contentResolver.openOutputStream(uri!!)
                    workbook.write(outputStream)
                    outputStream!!.flush()
                    //outputStream!!.write("This is menu category data.".toByteArray())
                    outputStream.close()
                    Toast.makeText(
                        context.applicationContext,
                        "Dosya Başarıyla Oluşturuldu",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: IOException) {
                    Toast.makeText(
                        context.applicationContext, "İşlem Başarısız!", Toast.LENGTH_SHORT
                    ).show()
                }

            } else {
                while (cursor.moveToNext()) {
                    val fileName: String =
                        cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME))
                    if (fileName == "$current.xls") {                          //must include extension
                        val id: Long =
                            cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
                        uri = ContentUris.withAppendedId(contentUri, id)
                        break
                    }
                }
                if (uri == null) {
                    Toast.makeText(
                        context.applicationContext,
                        "\"$current.xls\" Bulunamadı",
                        Toast.LENGTH_SHORT
                    ).show()

                    try {
                        val values = ContentValues()
                        values.put(
                            MediaStore.MediaColumns.DISPLAY_NAME, current
                        ) //file name
                        values.put(
                            MediaStore.MediaColumns.MIME_TYPE, "application/vnd.ms-excel"
                        ) //file extension, will automatically add to file
                        values.put(
                            MediaStore.MediaColumns.RELATIVE_PATH,
                            Environment.DIRECTORY_DOCUMENTS + "/Mezun Takip Listesi/"
                        ) //end "/" is not mandatory
                        uri = context.contentResolver.insert(
                            MediaStore.Files.getContentUri("external"), values
                        ) //important!
                        val outputStream = context.contentResolver.openOutputStream(uri!!)
                        workbook.write(outputStream)
                        outputStream!!.flush()
                        //outputStream!!.write("This is menu category data.".toByteArray())
                        outputStream.close()
                        Toast.makeText(
                            context.applicationContext,
                            "Dosya Başarıyla Oluşturuldu",
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: IOException) {
                        Toast.makeText(
                            context.applicationContext, "İşlem Başarısız!", Toast.LENGTH_SHORT
                        ).show()
                    }


                } else {
                    try {
                        val outputStream: OutputStream? = context.contentResolver.openOutputStream(
                            uri, "rwt"
                        ) //overwrite mode, see below
                        workbook.write(outputStream)
                        outputStream!!.flush()
                        //outputStream!!.write("This is menu category data.".toByteArray())
                        outputStream.close()
                        Toast.makeText(
                            context.applicationContext,
                            "Dosya Başarıyla Oluşturuldu",
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: IOException) {
                        Toast.makeText(
                            context.applicationContext, "İşlem Başarısız!", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }


    } else {
        val filePath = File(
            Environment.getExternalStorageDirectory().toString() + "/$current.xlsx"
        )
        try {
            if (!filePath.exists()) {
                filePath.createNewFile()
            }
            val fileOutputStream = FileOutputStream(filePath)
            workbook.write(fileOutputStream)
            Toast.makeText(
                context.applicationContext, "Dosya Başarıyla Oluşturuldu", Toast.LENGTH_SHORT
            ).show()
            fileOutputStream.flush()

            fileOutputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
            println(e)
        }
    }


}

fun addData(
    sheet: Sheet, context: Context, workbook: XSSFWorkbook, personList: ArrayList<Person>
) {

    val rowFake = sheet.createRow(0)

    CellUtil.createCell(rowFake, 0, "Ad Soyad")
    CellUtil.createCell(rowFake, 1, "Mezuniyet Yılı")
    CellUtil.createCell(rowFake, 2, "Bulunduğu İl")
    CellUtil.createCell(rowFake, 3, "Üniversite")
    CellUtil.createCell(rowFake, 4, "Mezuniyet Durumu")
    CellUtil.createCell(rowFake, 5, "Ek Not")
    CellUtil.createCell(rowFake, 6, "Telefon")
    CellUtil.createCell(rowFake, 7, "Email")
    CellUtil.createCell(rowFake, 8, "Fotoğraf Link")

    var a = 1
    for (i in personList) {
        val row = sheet.createRow(a)
        CellUtil.createCell(row, 0, i.name)
        CellUtil.createCell(row, 1, i.year.toString())
        CellUtil.createCell(row, 2, i.city)
        CellUtil.createCell(row, 3, i.school)

        if (i.graduation) {
            CellUtil.createCell(row, 4, "Mezun")
        } else {
            CellUtil.createCell(row, 4, "Mezun Değil")
        }
        CellUtil.createCell(row, 5, i.description)
        CellUtil.createCell(row, 6, "+90" + i.number.toString())
        CellUtil.createCell(row, 7, i.email)
        CellUtil.createCell(row, 8, i.photoURL)
        a += 1

    }

    createExcel(context, workbook)


}