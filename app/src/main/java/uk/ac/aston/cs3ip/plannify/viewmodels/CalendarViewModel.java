package uk.ac.aston.cs3ip.plannify.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.time.LocalDate;

public class CalendarViewModel extends ViewModel {
    // Google recommends using 1 ViewModel per View
    // reference: https://www.youtube.com/watch?v=Ts-uxYiBEQ8&t=520s

    // represents the date selected in the calendar
    private final MutableLiveData<LocalDate> selectedDate;
    // represents the date the user is currently viewing on the screen
    private final MutableLiveData<LocalDate> viewingDate;

    public CalendarViewModel() {
        selectedDate = new MutableLiveData<>();
        viewingDate = new MutableLiveData<>();
        selectedDate.setValue(LocalDate.now());
        viewingDate.setValue(LocalDate.now());
    }

    public MutableLiveData<LocalDate> getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(LocalDate selectedDate) {
        this.selectedDate.setValue(selectedDate);
    }

    public MutableLiveData<LocalDate> getViewingDate() {
        return viewingDate;
    }

    public void setViewingDate(LocalDate viewingDate) {
        this.viewingDate.setValue(viewingDate);
    }
}
