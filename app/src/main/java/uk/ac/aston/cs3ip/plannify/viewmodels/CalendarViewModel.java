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

    /**
     * Returns the selected date live data.
     *
     * @return the selected date.
     */
    public MutableLiveData<LocalDate> getSelectedDate() {
        return selectedDate;
    }

    /**
     * Sets the selected date live data.
     *
     * @param selectedDate selected date to set.
     */
    public void setSelectedDate(LocalDate selectedDate) {
        this.selectedDate.setValue(selectedDate);
    }

    /**
     * Returns the viewing date.
     *
     * @return the viewing date.
     */
    public MutableLiveData<LocalDate> getViewingDate() {
        return viewingDate;
    }

    /**
     * Sets the viewing date.
     *
     * @param viewingDate viewing date to set.
     */
    public void setViewingDate(LocalDate viewingDate) {
        this.viewingDate.setValue(viewingDate);
    }
}
