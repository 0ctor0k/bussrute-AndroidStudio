import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val selectedId: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
}