package com.hamzagulesci.contactbookapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.redmadrobot.inputmask.MaskedTextChangedListener;
import com.redmadrobot.inputmask.helper.AffinityCalculationStrategy;

import org.jetbrains.annotations.NotNull;

import kotlin.collections.CollectionsKt;

public class MainActivity extends AppCompatActivity {

    EditText editTextAd, editTextTelefon; // To get input from the user, Kullanıcıdan giriş almak için
    Button buttonKaydet, buttonListele;   // Buttons to start operations, İşlemleri başlatacak butonlar
    TextView textViewSonuc;               // To show the data, Verileri göstermek için
    DBHelper dbHelper;                    // Helper class for database operations, Veritabanı işlemleri için yardımcı sınıf
    EditText editTextId;
    Button buttonGuncelle, buttonSil;     // Box to enter ID for delete/update, Silme/güncelleme için ID girilecek kutu


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Load XML design to the screen, XML tasarımını ekrana yükle
        //  Identify components in XML, XML'deki bileşenleri tanımla
        editTextAd = findViewById(R.id.editTextAd);
        editTextTelefon = findViewById(R.id.editTextTelefon);
        buttonKaydet = findViewById(R.id.buttonKaydet);
        buttonListele = findViewById(R.id.buttonListele);
        textViewSonuc = findViewById(R.id.textViewSonuc);
        editTextId = findViewById(R.id.editTextId);
        buttonGuncelle = findViewById(R.id.buttonGuncelle);
        buttonSil = findViewById(R.id.buttonSil);


        // Initialize the Database helper class, Veritabanı yardımcı sınıfını başlat
        dbHelper = new DBHelper(this);


        // Processes that will run after clicking the Save button, Kaydet butonuna tıklanınca çalışacak işlemler
        buttonKaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ad = editTextAd.getText().toString().trim(); //  Get the name the user entered, Kullanıcının girdiği adı al
                String telefon = editTextTelefon.getText().toString().trim(); //  Get phone number, Telefon numarasını al

                // Alert the user if the name or phone field is empty, Eğer ad veya telefon alanı boşsa kullanıcıyı uyar
                if (ad.isEmpty() || telefon.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Leave no empty space, Boş alan bırakma", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Pattern to check if the phone number starts with 05 and has a total of 11 digits
                // Telefon numarasının 05 ile başlayıp toplam 11 hane olup olmadığını kontrol eden desen
                String regex = "^05\\d{9}$";

                // 📛 Alert if same phone, 📛 Aynı telefon varsa uyarı ver
                if (dbHelper.kisiVarMi(telefon)) {
                    Toast.makeText(MainActivity.this, "Registration with this number already exists, Bu numarayla kayıt zaten var", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Saving to the database, Veritabanına kaydetme işlemi
                boolean basarili = dbHelper.kisiEkle(ad, telefon);

                if (basarili) {
                    Toast.makeText(MainActivity.this, "Record added, Kayıt eklendi", Toast.LENGTH_SHORT).show();
                    editTextAd.setText("");      // Alanları temizle
                    editTextTelefon.setText("");
                } else {
                    Toast.makeText(MainActivity.this, "Failed to add record, Kayıt eklenemedi", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Processes that will run when the List button is clicked
        // Listele butonuna tıklanınca çalışacak işlemler
        buttonListele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor = dbHelper.tumKisileriGetir();

                if (cursor.getCount() == 0) {
                    textViewSonuc.setText("No record found, Kayıt bulunamadı.");
                    return;
                }

                StringBuilder sb = new StringBuilder();
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(0);         // id column, id sütunu
                    String ad = cursor.getString(1);   // name column, ad sütunu
                    String telefon = cursor.getString(2); // phone column, telefon sütunu

                    sb.append(id + ". " + ad + " / " + telefon + "\n");
                }

                textViewSonuc.setText(sb.toString());
                cursor.close(); // To avoid memory leaks, Bellek sızıntısı olmaması için
            }
        });

        buttonGuncelle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idText = editTextId.getText().toString();
                String ad = editTextAd.getText().toString();
                String telefon = editTextTelefon.getText().toString();

                if (idText.isEmpty() || ad.isEmpty() || telefon.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Fill in all fields, Tüm alanları doldurun", Toast.LENGTH_SHORT).show();
                    return;
                }

                int id = Integer.parseInt(idText);
                boolean guncelleSonuc = dbHelper.kisiGuncelle(id, ad, telefon);

                if (guncelleSonuc) {
                    Toast.makeText(MainActivity.this, "Record updated, Kayıt güncellendi", Toast.LENGTH_SHORT).show();
                    editTextAd.setText("");      // Alanları temizle
                    editTextTelefon.setText("");
                } else {
                    Toast.makeText(MainActivity.this, "Update failed, Güncelleme başarısız", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonSil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idText = editTextId.getText().toString();

                if (idText.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Enter ID to delete, Silmek için ID girin", Toast.LENGTH_SHORT).show();
                    return;
                }

                int id = Integer.parseInt(idText);
                boolean silSonuc = dbHelper.kisiSil(id);

                if (silSonuc) {
                    Toast.makeText(MainActivity.this, "Record deleted, Kayıt silindi", Toast.LENGTH_SHORT).show();
                    editTextAd.setText("");      // Clear fields, Alanları Temizle
                    editTextTelefon.setText("");
                    editTextId.setText("");
                } else {
                    Toast.makeText(MainActivity.this, "Failed to delete registration, Kayıt silinemedi", Toast.LENGTH_SHORT).show();
                }
            }
        });

        MaskedTextChangedListener listener = MaskedTextChangedListener.Companion.installOn(
                editTextTelefon,
                "+90 (5[00]) [000] [00] [00]",
                CollectionsKt.listOf(),
                AffinityCalculationStrategy.PREFIX,
                new MaskedTextChangedListener.ValueListener() {
                    @Override
                    public void onTextChanged(boolean maskFilled, @NotNull String extractedValue, @NotNull String formattedValue) {

                    }
                }
        );
    }
}
// github.com/hamzagulesci