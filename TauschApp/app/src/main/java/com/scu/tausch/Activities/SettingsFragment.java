package com.scu.tausch.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseFile;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.scu.tausch.DTO.OfferDTO;
import com.scu.tausch.Misc.Constants;
import com.scu.tausch.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    public SettingsFragment() {
        // Required empty public constructor
    }
    Bitmap bitmap;
    private EditText firstname,lastname, phonenumber;
    private TextView email;
    private ImageView picture;
    private Bitmap image;
    private final int REQUEST_CAMERA = 1;
    private final int SELECT_FILE = 2;
    private OfferDTO offerDTO;
    public static HomePage context;
    private boolean isOfferEditable=false;
    private Bitmap userImage;
    private ParseFile myImageFile;
    private String currentPhoneNumber;
    private ProgressDialog progress;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                return false;
            }
        });


        final ParseUser myCurrentUser = ParseUser.getCurrentUser();



        picture = (ImageView) rootView.findViewById(R.id.profile_picture);

        firstname = (EditText) rootView.findViewById(R.id.first_name);
        firstname.setText((String) myCurrentUser.get("firstname"));

        lastname = (EditText) rootView.findViewById(R.id.last_name);
        lastname.setText((String) myCurrentUser.get("lastname"));
        phonenumber = (EditText) rootView.findViewById(R.id.phone_number);
        phonenumber.setText((String) myCurrentUser.get("number"));
        currentPhoneNumber = phonenumber.getText().toString().trim();

        email = (TextView) rootView.findViewById(R.id.email);
        email.setText((String) myCurrentUser.get("email"));


        try {
            ParseFile bum = (ParseFile) ParseUser.getCurrentUser().get("picture");
            if (bum != null) {
                byte[] file = bum.getData();
                userImage = BitmapFactory.decodeByteArray(file, 0, file.length);
                picture.setImageBitmap(userImage);
            }
        } catch (ParseException pe) {

        }


        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();

            }
        });



        Button changeButton = (Button) rootView.findViewById(R.id.change_label);

        Button passwordButton = (Button) rootView.findViewById(R.id.buttonChangePassword);


        passwordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle("Update Password?");

                final EditText newpass = new EditText(getActivity());
                newpass.setHint("Enter new Password");
                newpass.setTransformationMethod(PasswordTransformationMethod.getInstance());

                final EditText newpass2 = new EditText(getActivity());
                newpass2.setHint("Re-enter Password");
                newpass2.setTransformationMethod(PasswordTransformationMethod.getInstance());


                LinearLayout layout = new LinearLayout(getActivity());
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.addView(newpass);
                layout.addView(newpass2);
                alertDialog.setView(layout);

                // alertDialog.setIcon(R.drawable.key);
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        if (newpass.getText().toString().equals(newpass2.getText().toString()) && !newpass.getText().toString().equals("")) {

                            myCurrentUser.setPassword(newpass.getText().toString());
                            myCurrentUser.saveInBackground();

                            Toast.makeText(getActivity(), "Success!!! Password changed.",
                                    Toast.LENGTH_LONG).show();

                        } else {
                            dialog.cancel();
                            Toast.makeText(getActivity(), "Password not changed.",
                                    Toast.LENGTH_LONG).show();
                        }

                    }
                });

                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                        Toast.makeText(getActivity(), "Password not changed.",
                                Toast.LENGTH_LONG).show();
                    }
                });
                alertDialog.show();

            }


        });


        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Bitmap bitmap = ((BitmapDrawable)currentImageView.getDrawable()).getBitmap();

                progress = new ProgressDialog(getActivity());
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setIndeterminate(true);
                progress.show();


                bitmap = ((BitmapDrawable)picture.getDrawable()).getBitmap();


                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // Compress image to lower quality scale 1 - 100
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] image = stream.toByteArray();

                // Create the ParseFile
                ParseFile file = new ParseFile("image.png", image);
                // Upload the image into Parse Cloud

                if (file.isDataAvailable()) {
                    file.saveInBackground();
                }



                myCurrentUser.put("firstname",firstname.getText().toString());
                myCurrentUser.put("lastname",lastname.getText().toString());
                myCurrentUser.put("number",phonenumber.getText().toString());


                if (!checkIfPhoneNumberIsValid(phonenumber.getText().toString().trim())) {
                    showDialogForIncorrectNumber();
                    phonenumber.setText(currentPhoneNumber);
                    return;
                }


                if (myImageFile!=null) {
                  //  myCurrentUser.put("picture", file);
                }
                myCurrentUser.put("picture", file);

             //   myCurrentUser.saveInBackground();

                SaveCallback sc = new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e==null){
                            Toast.makeText(getActivity(), "Profile Information Updated",
                                    Toast.LENGTH_LONG).show();
                            progress.dismiss();
                        }
                        else{
                            Toast.makeText(getActivity(), "Profile Update failed.",
                                    Toast.LENGTH_LONG).show();
                            progress.dismiss();
                        }
                    }
                };


//                Toast.makeText(getActivity(), "Profile Information Updated",
//                        Toast.LENGTH_LONG).show();
                myCurrentUser.saveInBackground(sc);


            }
        });


        // Inflate the layout for this fragment
        return rootView;
    }


    private boolean checkIfPhoneNumberIsValid(String val) {


        if (val.toString().trim().length() != 10) {

            return false;
        }
        return true;
    }

    private void showDialogForIncorrectNumber() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("Please enter valid phone number.");

        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    //Fetching image and applying it to imageView after compressing it.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                //   thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                File destination = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    bytes.close();
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                picture.setImageBitmap(thumbnail);
                if (HomePage.profilePicture != null) {
                    HomePage.profilePicture.setImageBitmap(thumbnail);
                }
            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                String[] projection = {MediaStore.MediaColumns.DATA};
                CursorLoader cursorLoader = new CursorLoader(getActivity(), selectedImageUri, projection, null, null, null);
                Cursor cursor = cursorLoader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                String selectedImagePath = cursor.getString(column_index);
                Bitmap bm;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 200;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                if (selectedImagePath!=null) {
                    bm = BitmapFactory.decodeFile(selectedImagePath, options);
                    picture.setImageBitmap(bm);

                    if (HomePage.profilePicture != null) {
                        HomePage.profilePicture.setImageBitmap(bm);
                    }
                }
                else{
                    Toast.makeText(getActivity(), "Image update failed.",
                            Toast.LENGTH_LONG).show();

                }
            }
        }

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Constants.CURRENT_SCREEN = Constants.SCREEN_SETTINGS;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
