package com.myapplicationdev.android.p07_smsretriever;

import android.Manifest;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

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


/**
 * A simple {@link Fragment} subclass.
 */
public class WordFragment extends Fragment {

    Button btnWords;
    TextView tvWords;
    EditText etWords;

    public WordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_word, container, false);

        tvWords = view.findViewById(R.id.tvWords);
        etWords = view.findViewById(R.id.etWords);
        btnWords = view.findViewById(R.id.btnWords);

        btnWords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String word = etWords.getText().toString();

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

                String filter = "body LIKE ?";
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


                tvWords.setText(smsBody);

            }
        });

        return view;

    }
}
