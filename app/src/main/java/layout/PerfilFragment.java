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

        Bundle args = getArguments();
        String photoUrl = args.getString("photo", "No photo");
        String name = args.getString("name", "No name");
        String email = args.getString("email", "No mail");
        String dni = args.getString("dni", "No dni");

        if (photoUrl != "0"){
            Glide.with(getActivity()).load(photoUrl).into(imageView);
        } else {
            imageView.getResources().getDrawable(R.drawable.img_hombre);
        }

        if (dni != "No dni"){
            txtDni.setText("DNI: "+dni);
            txtDni.setVisibility(View.VISIBLE);
        }

        txtName.setText(name);
        txtMail.setText(email);

        return view;
    }

}
