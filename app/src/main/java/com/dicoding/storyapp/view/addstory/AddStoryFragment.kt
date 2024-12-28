package com.dicoding.storyapp.view.addstory

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.FragmentAddStoryBinding
import com.dicoding.storyapp.utils.getImageUri
import com.dicoding.storyapp.utils.reduceFileImage
import com.dicoding.storyapp.utils.uriToFile
import com.dicoding.storyapp.view.ViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AddStoryFragment : Fragment() {
    private var _binding: FragmentAddStoryBinding? = null
    private val viewModel: AddStoryViewModel by viewModels{
        ViewModelFactory.getInstance(requireContext())
    }

    private var currentImageUri: Uri? = null
    private var currentLocation: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showToast(getString(R.string.permission_request_granted))
            } else {
                showToast(getString(R.string.permission_request_denied))
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            requireContext(),
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED


    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    currentLocation = location
                    Log.d("Location", "getMyLocation: $location")
                }
        } else {
            _binding?.switchLocation?.isChecked ?: false
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_add_story, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentAddStoryBinding.bind(view)
        _binding = binding

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.buttonAdd.setOnClickListener { uploadImage() }

        binding.switchLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                getMyLocation()
            }
        }

        setupViewModel()
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            _binding?.previewImageView?.setImageURI(it)
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        } else {
            currentImageUri = null
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(requireContext())
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private fun uploadImage() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, requireContext()).reduceFileImage()
            val description = _binding?.edAddDescription?.text.toString()

            if (description.isEmpty()) {
                showToast(getString(R.string.empty_description_warning))
                return
            }

            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )

            viewModel.uploadStory(
                description = requestBody,
                imageMultipart = multipartBody,
                lat = if (_binding?.switchLocation?.isChecked == true) currentLocation?.latitude else null,
                lon = if (_binding?.switchLocation?.isChecked == true) currentLocation?.longitude else null
            )

        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private fun setupViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        viewModel.uploadResult.observe(viewLifecycleOwner) { result ->
            result.fold(
                onSuccess = { response ->
                    Log.d("Upload Success", "Message: ${response.message}")
                    showToast(response.message ?: getString(R.string.upload_success))
                    Handler(Looper.getMainLooper()).postDelayed({
                        if (isAdded) {
                            findNavController().navigate(R.id.navigation_home)
                        }
                    }, 3000)
                },
                onFailure = { exception ->
                    showToast(exception.message ?: getString(R.string.upload_error))
                }
            )
        }
    }

    private fun showLoading(isLoading: Boolean) {
        _binding?.apply {
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            buttonAdd.isEnabled = !isLoading
        }
    }

    private fun showToast(message: String) {
        val toast = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.BOTTOM, 0, 150)
        toast.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}
