package layout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import tecsup.integrador.gamarraapp.R;


public class PerfilFragment extends Fragment {

    private ImageView imageView;
    private TextView txtName;
    private TextView txtMail;
    private TextView txtDni;

    Animation uptowndown, downtoup;

    public PerfilFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        imageView = (ImageView) view.findViewById(R.id.imageView);
        txtName = (TextView) view.findViewById(R.id.name);
        txtMail = (TextView) view.findViewById(R.id.email);
        txtDni = (TextView) view.findViewById(R.id.dni);

        uptowndown = AnimationUtils.loadAnimation(getActivity(),R.anim.uptodown);
        downtoup = AnimationUtils.loadAnimation(getActivity(),R.anim.downtoup);

        Bundle args = getArguments();
        String photoUrl = args.getString("photo", "No photo");
        String name = args.getString("name", "No name");
        String email = args.getString("email", "No mail");
        String dni = args.getString("dni", "No dni");

        if (photoUrl != "0"){
            Glide.with(getActivity()).load(photoUrl).into(imageView);
            imageView.setAnimation(uptowndown);
        } else {
            imageView.getResources().getDrawable(R.drawable.img_hombre);
            imageView.setAnimation(uptowndown);
        }

        if (dni != "No dni"){
            txtDni.setText("DNI: "+dni);
            txtDni.setVisibility(View.VISIBLE);
            txtDni.setAnimation(downtoup);
        }

        txtName.setText(name);
        txtMail.setText(email);

        txtName.setAnimation(downtoup);
        txtMail.setAnimation(downtoup);

        return view;
    }

}
