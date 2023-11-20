package group10.com.guesstheera.mainview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import group10.com.guesstheera.databinding.FragmentGameOptionsBinding

class GameOptionsFragment: Fragment() {
    private var _binding: FragmentGameOptionsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentGameOptionsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }
}