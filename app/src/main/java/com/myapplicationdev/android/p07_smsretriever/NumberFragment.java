package com.myapplicationdev.android.p07_smsretriever;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class NumberFragment extends Fragment {

    Button btnNumber;
    EditText etNumb;
    TextView tvNumb;

    public NumberFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_number, container, false);

        etNumb = view.findViewById(R.id.etNumb);
        tvNumb = view.findViewById(R.id.tvNumb);
        btnNumber = view.findViewById(R.id.btnNumb);

        btnNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String word = etNumb.getText().toString();
                int pCheck = PermissionChecker.checkSelfPermission
                        (getActivity(), Manifest.permission.READ_SMS);
                if(pCheck != PermissionChecker.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_SMS}, 0);
                    return;
                }

                Uri uri = Uri.parse("content://sms");
                String[] reqCols = new String[]{"date",
                "address", "body", "type"};

                ContentResolver cr = getActivity().getContentResolver();

                String filter = "address LIKE ?";
                String[] argsf = {"%" + word + "%"};
                Cursor cursor = cr.query(uri, reqCols, filter, argsf, null);
                String smsBody = "";
                if (cursor.moveToFirst()) {
                    do {
                        long dateInMillis = cursor.getLong(0);
                        String date = (String) DateFormat
                                .format("dd MMM yyyy h:m:ss aa", dateInMillis);
                        String address = cursor.getString(1);
                        String body = cursor.getString(2);
                        String type = cursor.getString(3);
                        if(type.equalsIgnoreCase("1")){
                            type = "Inbox:";
                        } else {
                            type = "Sent";
                        }
                        smsBody += type + " " + address + "\n at " + date
                                + "\n\"" + body + "\"\n\n";

                    } while (cursor.moveToNext());
                }



                tvNumb.setText(smsBody);
            }
        });
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    btnNumber.performClick();
                } else {
                    Toast.makeText(getActivity(),
                            "Permission not grante",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
