package co.id.ramadanrizky.todofirebasefix3.fragment;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import androidx.fragment.app.FragmentTransaction;
import co.id.ramadanrizky.todofirebasefix3.LoginActivity;
import co.id.ramadanrizky.todofirebasefix3.MainActivity;
import co.id.ramadanrizky.todofirebasefix3.R;
import co.id.ramadanrizky.todofirebasefix3.SettingsActivity;
import co.id.ramadanrizky.todofirebasefix3.pojo.UserTodo;
import co.id.ramadanrizky.todofirebasefix3.share_pref.SharedPreferencesManager;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static co.id.ramadanrizky.todofirebasefix3.MainActivity.PROF_TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private SharedPreferencesManager prefm;
    private TextView txt_logout, txt_email_address, txt_change_password,txt_settings;
    private EditText edt_username, edt_address;
    private ImageView img_changeUsername, img_doneUsername, img_renew;
    private CircleImageView img_profile;
    private static final int IMAGE_REQUEST = 1;
    private Uri uriOfImage;
    private StorageReference storgRef;
    private StorageTask strgTask;
    FirebaseUser user;
    DatabaseReference reference;
    FirebaseAuth auth;
    UserTodo userTodo;
    GoogleSignInClient googleSignInClient;

    //Dialog
    private EditText edt_old_pass, edt_new_pass;
    private AlertDialog.Builder builder;
    private LayoutInflater inflater;
    View dView;
    String old_pass, new_pass;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        initView(view);
        loadNightMode();
        setTransparentStatusBar();
//        switchChecked();
        setFirebase();
        listener();


        return view;
    }


    private void setFirebase() {

            reference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (isAdded()){
                        userTodo = dataSnapshot.getValue(UserTodo.class);
                        setUsername(userTodo);
                        setEmailAddress(userTodo);
                        setAddress(userTodo);
                        setProfilePicture(userTodo);

                        if (userTodo.getTypeLogin() == 1){
                            txt_change_password.setVisibility(View.GONE);
                        }else {
                            txt_change_password.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


    }



    private void setProfilePicture(UserTodo userTodo) {
        if (userTodo.getImageUrl().equals("default")){
            img_profile.setImageResource(R.drawable.na);
        }else {
            Glide.with(getContext())
                    .load(userTodo.getImageUrl())
                    .apply(new RequestOptions().override(100,100))
                    .into(img_profile);
        }
    }

    private void setEmailAddress(UserTodo userTodo) {
        String email = userTodo.getEmail();
        txt_email_address.setText(email);
    }

    private void setAddress(UserTodo userTodo) {
        String username = userTodo.getAddress();
        String usernameCapitalize = username.substring(0,1).toUpperCase() + username.substring(1);
        edt_address.setText(usernameCapitalize);
    }

    private void setUsername(UserTodo userTodo) {
        String username = userTodo.getUsername();
        String usernameCapitalize = username.substring(0,1).toUpperCase() + username.substring(1);
        edt_username.setText(usernameCapitalize);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            uriOfImage = data.getData();

            if (strgTask != null && strgTask.isInProgress()){
                Toast.makeText(getContext(), "Upload...", Toast.LENGTH_SHORT).show();
            }else {
                uploadsImage();
            }

        }
    }

    private void uploadsImage() {
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Uploading...");
        pd.show();

        if (uriOfImage != null){
            final StorageReference imageReference = storgRef.child(System.currentTimeMillis()+"."+getFileExtension(uriOfImage));

            strgTask = imageReference.putFile(uriOfImage);
            strgTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return imageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        reference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
                        HashMap<String, Object>map = new HashMap<>();
                        map.put("imageUrl", mUri);
                        reference.updateChildren(map);

                        pd.dismiss();
                    }else {
                        Toast.makeText(getContext(), "Failed upload image", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }else {
            Toast.makeText(getContext(), "No Image Selected", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uriOfImage) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap typeMap = MimeTypeMap.getSingleton();
        return typeMap.getExtensionFromMimeType(contentResolver.getType(uriOfImage));
    }

    //onClick
    private void listener() {

        txt_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDialogLogout();
            }
        });

        img_changeUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Edit", Toast.LENGTH_SHORT).show();
                img_changeUsername.setVisibility(View.GONE);
                img_doneUsername.setVisibility(View.VISIBLE);
                edt_username.setEnabled(true);
                edt_address.setEnabled(true);
                img_profile.setEnabled(true);
                edt_username.requestFocus();
                edt_username.setFocusableInTouchMode(true);
                img_renew.setVisibility(View.VISIBLE);

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(edt_username, InputMethodManager.SHOW_FORCED);
                DrawableCompat.setTint(edt_username.getBackground(), ContextCompat.getColor(getContext(), R.color.colorPrimary));
                DrawableCompat.setTint(edt_address.getBackground(), ContextCompat.getColor(getContext(), R.color.colorAccent));

            }
        });
        img_doneUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(edt_username.getText())){
                    edt_username.setError("Required");
                }else {
                    String username = edt_username.getText().toString();
                    changeUsername(username);
                }

                if (TextUtils.isEmpty(edt_address.getText())){
                    edt_address.setError("Required");
                }else {
                    String address = edt_address.getText().toString();
                    changeAddress(address);
                }

            }
        });
        img_renew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, IMAGE_REQUEST);
            }
        });

        img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edt_username.requestFocus();
                edt_username.setFocusableInTouchMode(false);
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, IMAGE_REQUEST);
            }
        });
        txt_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(userTodo);
            }
        });
        txt_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), SettingsActivity.class));
            }
        });
    }

    private void showDialogLogout() {
        AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
        build.setTitle("Logout ?");
        build.setMessage("Are you sure want Logout?");
        build.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                prefm.setKeyStatusLoggedIn(false);
                auth.signOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
                getActivity().finish();


                //logout google account
                googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        prefm.setKeyStatusLoggedIn(false);
                        startActivity(new Intent(getContext(), LoginActivity.class));
                        getActivity().finish();
                    }
                });
                dialogInterface.dismiss();
            }
        });
        build.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        build.show();
    }

    private void showDialog(final UserTodo userTodo) {

        builder = new AlertDialog.Builder(getActivity());

        if (dView.getParent()!= null){
            ((ViewGroup)dView.getParent()).removeView(dView);
        }
        builder.setView(dView);
        builder.setCancelable(true);


        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                old_pass = edt_old_pass.getText().toString();
                new_pass = edt_new_pass.getText().toString();
                user = FirebaseAuth.getInstance().getCurrentUser();
                final String email = user.getEmail();
                final AuthCredential credential = EmailAuthProvider.getCredential(email, old_pass);


                if (TextUtils.isEmpty(edt_old_pass.getText().toString()) || edt_old_pass.getText().length() < 8 || TextUtils.isEmpty(edt_new_pass.getText().toString()) || edt_new_pass.getText().length() < 8) {
                    edt_old_pass.setError("Password Required and must 8 character");
                    edt_new_pass.setError("Password Required and must 8 character");
                    if (dView.getParent()!= null){
                        ((ViewGroup)dView.getParent()).removeView(dView);
                    }
                    builder.show();
                }else {
                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                user.updatePassword(new_pass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            edt_new_pass.setText("");
                                            edt_old_pass.setText("");
                                            Toast.makeText(getContext(), "Password Updated", Toast.LENGTH_SHORT).show();
                                            dialogInterface.dismiss();

                                        }else {
                                            edt_new_pass.setText("");
                                            edt_old_pass.setText("");
                                            Toast.makeText(getContext(), "Failed Update Password", Toast.LENGTH_SHORT).show();
                                            dialogInterface.dismiss();
                                        }
                                    }
                                });
                            }else {
                                edt_new_pass.setText("");
                                edt_old_pass.setText("");
                                Toast.makeText(getContext(), "Old Password not Match", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                edt_new_pass.setText("");
                edt_old_pass.setText("");
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void changeAddress(String address) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        reference.child("users").child(user.getUid())
                .child("address")
                .setValue(address)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        edt_address.setEnabled(false);
                        Intent i = new Intent(getContext(), MainActivity.class);
                        startActivity(i);
                        getActivity().finish();
                    }
                });
    }

    private void changeUsername(String username) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        reference.child("users").child(user.getUid())
                .child("username")
                .setValue(username)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
//                        Toast.makeText(getContext(), "Username Updated :)", Toast.LENGTH_SHORT).show();
                        img_doneUsername.setVisibility(View.GONE);
                        img_changeUsername.setVisibility(View.VISIBLE);
                        DrawableCompat.setTint(edt_username.getBackground(), ContextCompat.getColor(getContext(), R.color.colorAccent));
                        edt_username.setEnabled(false);
                        Intent i = new Intent(getContext(), MainActivity.class);
                        startActivity(i);
                        getActivity().finish();
                    }
                });

    }

    //viewDeclaration
    private void initView(View view) {

        googleSignInClient = GoogleSignIn.getClient(getContext(), GoogleSignInOptions.DEFAULT_SIGN_IN);
        auth                = FirebaseAuth.getInstance();
        storgRef            = FirebaseStorage.getInstance().getReference("user_pict");
        user                = FirebaseAuth.getInstance().getCurrentUser();
        reference           = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
//        switchNight         = (Switch) view.findViewById(R.id.switchNight);
        prefm               = new SharedPreferencesManager(getContext());
        txt_logout          = view.findViewById(R.id.txt_logout);
        edt_username        = view.findViewById(R.id.txt_username);
        img_changeUsername  = view.findViewById(R.id.img_editusername);
        img_doneUsername    = view.findViewById(R.id.img_doneusername);
        edt_address         = view.findViewById(R.id.address);
        txt_email_address   = view.findViewById(R.id.txt_emailAddress);
        img_profile         = view.findViewById(R.id.img_profile);
        txt_change_password = view.findViewById(R.id.change_pass);
        txt_settings        = view.findViewById(R.id.txt_settings);
        img_renew           = view.findViewById(R.id.img_renew);

        //alert dialog init
        inflater        = getLayoutInflater();
        dView           = inflater.inflate(R.layout.change_password_form, null);
        edt_old_pass    = dView.findViewById(R.id.edt_old_pass);
        edt_new_pass    = dView.findViewById(R.id.edt_new_pass);

        edt_address.setEnabled(false);
        edt_username.setEnabled(false);
        img_profile.setEnabled(false);


    }

    //set status bar to transparent
    private void setTransparentStatusBar() {
        getActivity().getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
    }

    //check Night Mode
    private void loadNightMode() {
        if (prefm.loadNightModeState()==true){
//            switchNight.setChecked(true);
            txt_settings.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_settings_white_24dp,0,0,0);
            edt_address.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location_city_black_24dp,0,0,0);
            txt_logout.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_arrow_back_white_24dp,0,0,0);
            txt_email_address.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_email_white_24dp,0,0,0);
            txt_change_password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock_white_24dp,0,0,0);
            edt_old_pass.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock_white_24dp,0,0,0);
            edt_new_pass.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock_white_24dp,0,0,0);
        }else {
//            switchNight.setChecked(false);
            txt_settings.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_settings_black_24dp,0,0,0);
            edt_address.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location_city_bc_24dp,0,0,0);
            txt_logout.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_arrow_back_black_24dp,0,0,0);
            txt_email_address.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_email_bck_24dp,0,0,0);
            txt_change_password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock_bck_24dp,0,0,0);
            edt_old_pass.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock_bck_24dp,0,0,0);
            edt_new_pass.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock_bck_24dp,0,0,0);
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        loadNightMode();
    }


}
