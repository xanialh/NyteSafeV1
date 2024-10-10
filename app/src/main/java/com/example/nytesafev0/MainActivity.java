package com.example.nytesafev0;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MainActivity extends AppCompatActivity {

    private CheckBox checkBox;
    private Button buttonAcceptTerms;
    private MaterialAlertDialogBuilder materialAlertDialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkBox = findViewById(R.id.terms_id);
        buttonAcceptTerms = findViewById(R.id.buttonAcceptTerms);
        materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this);

        buttonAcceptTerms.setEnabled(false);

        buttonAcceptTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    materialAlertDialogBuilder.setTitle("Terms and Conditions");
                    // Add your Terms and Conditions content here
                    materialAlertDialogBuilder.setMessage("Your Terms and Conditions message here");
                    materialAlertDialogBuilder.setMessage("Acceptance of Terms\n" +
                            "These Terms and Conditions of Use (\"Terms\") govern your use of the NyteSafe App (\"App\"), which is owned and operated by NyteSafe, LLC (\"NyteSafe\"). By downloading, accessing, or using the App, you agree to be bound by these Terms, as well as any additional terms and conditions that may apply to specific sections or features of the App.\n" +
                            "\n" +
                            "Use of the App\n" +
                            "The App is intended for personal and non-commercial use only. You may not use the App in any way that could damage, disable, overburden, or impair the App's servers or networks. You may not attempt to interfere with the proper working of the App or bypass any security measures or access controls implemented by NyteSafe.\n" +
                            "\n" +
                            "Registration and User Accounts\n" +
                            "In order to access certain features of the App, you may need to create a user account and provide personal information, such as your name and email address. You are responsible for maintaining the confidentiality of your login credentials and for all activities that occur under your account. You agree to notify NyteSafe immediately of any unauthorized use of your account or any other breach of security.\n" +
                            "\n" +
                            "Content and Intellectual Property\n" +
                            "The App and its contents, including but not limited to text, graphics, images, and software, are protected by copyright, trademark, and other intellectual property laws. You may not reproduce, modify, distribute, or display any of the content of the App without the prior written consent of NyteSafe.\n" +
                            "\n" +
                            "User-Generated Content\n" +
                            "The App may allow users to post or upload content, such as reviews, comments, and ratings. By posting or uploading any content, you represent and warrant that you have the right to do so and that the content does not infringe upon the intellectual property rights of any third party. NyteSafe reserves the right to remove any content that violates these Terms or that NyteSafe deems to be inappropriate, offensive, or harmful in any way.\n" +
                            "\n" +
                            "Disclaimer of Warranties\n" +
                            "THE APP IS PROVIDED \"AS IS\" AND WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED. NYTESAFE DOES NOT WARRANT THAT THE APP WILL BE UNINTERRUPTED OR ERROR-FREE, THAT DEFECTS WILL BE CORRECTED, OR THAT THE APP OR ITS SERVERS ARE FREE OF VIRUSES OR OTHER HARMFUL COMPONENTS. NYTESAFE MAKES NO REPRESENTATIONS OR WARRANTIES OF ANY KIND, EXPRESS OR IMPLIED, AS TO THE OPERATION OF THE APP OR THE INFORMATION, CONTENT, OR MATERIALS INCLUDED ON THE APP.\n" +
                            "\n" +
                            "Limitation of Liability\n" +
                            "NYTESAFE SHALL NOT BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, OR CONSEQUENTIAL DAMAGES ARISING OUT OF OR IN CONNECTION WITH YOUR USE OF THE APP, INCLUDING BUT NOT LIMITED TO DAMAGES FOR LOSS OF PROFITS, USE, DATA, OR OTHER INTANGIBLES, EVEN IF NYTESAFE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. SOME JURISDICTIONS DO NOT ALLOW THE LIMITATION OR EXCLUSION OF LIABILITY FOR INCIDENTAL OR CONSEQUENTIAL DAMAGES, SO THE ABOVE LIMITATION OR EXCLUSION MAY NOT APPLY TO YOU.\n" +
                            "\n" +
                            "Indemnification\n" +
                            "You agree to indemnify and hold NyteSafe harmless from and against any and all claims, damages, liabilities, costs, and expenses, including reasonable attorneys' fees, arising out of or in connection with your use of the App or any breach of these Terms.\n" +
                            "\n" +
                            "Modification of Terms\n" +
                            "NyteSafe reserves the right to modify these Terms at any time by posting the modified terms on the App. Your continued use of the App following any such modification constitutes your agreement to be");
                    materialAlertDialogBuilder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            buttonAcceptTerms.setEnabled(true);
                            dialog.dismiss();
                        }
                    });
                    materialAlertDialogBuilder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            checkBox.setChecked(false);
                        }
                    });
                    materialAlertDialogBuilder.show();
                } else {
                    buttonAcceptTerms.setEnabled(false);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuSettings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_menu, menu);
        return true;
    }
}
