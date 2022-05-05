package com.bangkit.story.ui.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bangkit.story.R
import com.bangkit.story.databinding.ActivityNewStoryBinding
import com.bangkit.story.ui.viewmodel.NewStoryViewModel
import com.bangkit.story.ui.viewmodel.ViewModelFactory
import com.bangkit.story.utils.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class NewStoryActivity : AppCompatActivity() {

    private var getFile: File? = null
    private lateinit var currentPhotoPath: String
    private lateinit var binding: ActivityNewStoryBinding
    private var latitude: Float? = null
    private var longitude: Float? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val newStoryViewModel: NewStoryViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        initViewBinding()
        initToolbar()
        checkAllPermissions()
        onButtonPressed()
        getMyLastLocation()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(this, getString(R.string.permission_error), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun initViewBinding() {
        binding = ActivityNewStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun initToolbar() {
        supportActionBar?.apply {
            this.title = getString(R.string.add_new_story)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun checkAllPermissions() {
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun onButtonPressed() {
        binding.apply {
            cameraButton.setOnClickListener { startTakePhoto() }
            galleryButton.setOnClickListener { startGallery() }
            uploadButton.setOnClickListener { uploadImage() }
        }
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@NewStoryActivity,
                getString(R.string.package_name),
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_picture))
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentCamera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val myFile = File(currentPhotoPath)
                val rotate = rotateBitmap(BitmapFactory.decodeFile(myFile.path), true)
                val temp = getImageUri(rotate, this)
                getFile = uriToFile(temp, this)
                binding.previewImageView.setImageBitmap(rotate)
            }
        }

    private val launcherIntentGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val selectedImg: Uri = result.data?.data as Uri
                val myFile = uriToFile(selectedImg, this@NewStoryActivity)
                getFile = myFile
                binding.previewImageView.setImageURI(selectedImg)
            }
        }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    // Precise location access granted.
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    // Only approximate location access granted.
                    getMyLastLocation()
                }
                else -> {
                    // No location access granted.
                }
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if(checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)){
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    latitude = location.latitude.toFloat()
                    longitude = location.longitude.toFloat()
                } else {
                    Toast.makeText(this, getString(R.string.loc_not_found), Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    private fun uploadImage() {
        val description = binding.descriptionEditText.text.toString()
        when {
            description.isEmpty() -> binding.descriptionEditText.error = getString(R.string.description_required_error)
            getFile == null -> Toast.makeText(this, getString(R.string.image_required_error), Toast.LENGTH_SHORT).show()
            else -> {
                val file = reduceFileImage(getFile as File)
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart = MultipartBody.Part.createFormData(
                    getString(R.string.photo),
                    file.name,
                    requestImageFile
                )
                initObserver(description.toRequestBody(), imageMultipart, latitude, longitude)
            }
        }
    }

    private fun initObserver(description: RequestBody, file: MultipartBody.Part, lat: Float?, lon: Float?) {
        newStoryViewModel.addNewStory(description, file, lat, lon).observe(this) { response ->
            when (response) {
                is State.Loading -> {
                    binding.apply {
                        loadingUpload.visibility = View.VISIBLE
                        uploadButton.visibility = View.INVISIBLE
                    }
                }
                is State.Success -> {
                    binding.apply {
                        loadingUpload.visibility = View.INVISIBLE
                        uploadButton.visibility = View.VISIBLE
                    }
                    Toast.makeText(this, response.data.message, Toast.LENGTH_SHORT).show()
                    moveToMainActivity()
                }
                is State.Error -> {
                    binding.apply {
                        loadingUpload.visibility = View.INVISIBLE
                        uploadButton.visibility = View.VISIBLE
                    }
                    Toast.makeText(this, response.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun moveToMainActivity() {
        val toMain = Intent(this, MainActivity::class.java)
        toMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(toMain)
        finish()
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}