package com.example.noteappandroid.ui.create_notes

import android.app.AlarmManager
import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StyleRes
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.noteappandroid.R
import com.example.noteappandroid.base.BaseDialogFragment
import com.example.noteappandroid.databinding.FragmentNoteReminderDialogBinding
import com.example.noteappandroid.entity.Folder
import com.example.noteappandroid.entity.MyColor
import com.example.noteappandroid.entity.NoteEntity
import com.example.noteappandroid.utils.cancelAlarm
import com.example.noteappandroid.utils.createAlarm
import com.example.noteappandroid.utils.format
import com.example.noteappandroid.utils.setRippleColor
import com.example.noteappandroid.utils.snackbar
import com.example.noteappandroid.utils.stringResource
import com.example.noteappandroid.utils.toColorResourceId
import com.example.noteappandroid.utils.toColorStateList
import com.example.noteappandroid.utils.toLocalDate
import com.example.noteappandroid.utils.toLocalTime
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import java.text.SimpleDateFormat

private const val DatePickerDialogTag = "DatePickerDialog"
private const val TimePickerDialogTag = "TimePickerDialog"

@AndroidEntryPoint
class NoteReminderDialog : BaseDialogFragment() {
    private lateinit var binding: FragmentNoteReminderDialogBinding

    private val viewModel by viewModels<CreateNoteViewModel>()
    private val args by navArgs<NoteReminderDialogArgs>()

    private val alarmManager by lazy { context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager? }

    private val is24HourFormat by lazy { DateFormat.is24HourFormat(context) }

    private val timeFormat by lazy { if (is24HourFormat) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H }

    private val parentView by lazy { parentFragment?.view }

//    private val folderColor by lazy { viewModel.folder.value.color }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNoteReminderDialogBinding.inflate(inflater, container, false)
        viewModel.setNoteId(args.noteId)
        setupState()
        setupListeners()
        return binding.root
    }

    private fun setupListeners() {
        binding.btnSet.setOnClickListener {
            context?.let { context ->
                viewModel.setNoteReminder()
                val instant = viewModel.reminderDateTime.value
                val date = instant.toLocalDate().format(lowercaseTimeSpan = true)
                val time = instant.toLocalTime().format(is24HourFormat)
                val stringId = R.string.reminder_is_set
                val drawableId = R.drawable.ic_round_notifications_active_24
                val note = viewModel.note.value
                note?.id?.let { it1 ->
//                    note.folderId.let { it2 ->
//                        if (it2 != null) {
                    alarmManager?.createAlarm(
                        context,
                        null,
                        it1,
                        instant.toEpochMilliseconds()
                    )
//                        }
//                    }
                }
                parentView?.snackbar(
                    context.stringResource(stringId, date, time),
                    drawableId,
//                    folderColor
                )
            }
            dismiss()
        }

        binding.btnCancel.setOnClickListener {
            context?.let { context ->
                val stringId = R.string.reminder_is_cancelled
                val drawableId = R.drawable.ic_round_cancel_24
                viewModel.cancelNoteReminder()
                viewModel.note.value?.id?.let { it1 -> alarmManager?.cancelAlarm(context, it1) }
                parentView?.snackbar(
                    context.resources.getString(stringId),
                    drawableId,
//                    anchorViewId,
//                    folderColor
                )
            }
            dismiss()
        }
    }

    private fun createDatePickerDialog(
        milliseconds: Long,
        @StyleRes themeResId: Int
    ): MaterialDatePicker<Long> {
        val dateFormat = DateFormat.getDateFormat(context) as SimpleDateFormat
        val localizedDateFormat = dateFormat.toLocalizedPattern().let(::SimpleDateFormat)
        val calendarConstraints = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now())
            .build()

        return MaterialDatePicker.Builder.datePicker()
            .setSelection(milliseconds)
            .setTitleText(context?.resources?.getString(R.string.reminder_date))
            .setTheme(themeResId)
            .setTextInputFormat(localizedDateFormat)
            .setCalendarConstraints(calendarConstraints)
            .build()
            .apply {
                addOnPositiveButtonClickListener {
                    selection?.let { epochMilliseconds ->
                        viewModel.setReminderDate(epochMilliseconds)
                    }
                }
            }
    }

    private fun setupState() {
        viewModel.note
            .onEach { note ->
                if (note != null) {
                    setupNote(note)
                }
            }
            .launchIn(lifecycleScope)

//        combine(viewModel.folder, viewModel.reminderDateTime) { folder, instant ->
        viewModel.reminderDateTime.onEach { instant ->
            val date = instant.toLocalDate()
            val time = instant.toLocalTime()
            binding.tvDateValue.text = date.format()
            binding.tvTimeValue.text = time.format(is24HourFormat)

            Log.d("TAG", "date: ${date.format()}")
            Log.d("TAG", "time: ${time.format(is24HourFormat)}")

        }.launchIn(lifecycleScope)
        binding.llDate.setOnClickListener {
            val epochMilliseconds = viewModel.reminderDateTime.value
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .toInstant(TimeZone.UTC)
                .toEpochMilliseconds()
            val datePickerDialogTheme = viewModel.folder.value.color.toDatePickerDialogTheme()
            val datePickerDialog = createDatePickerDialog(epochMilliseconds, datePickerDialogTheme)
            val isDialogShown =
                parentFragmentManager.findFragmentByTag(DatePickerDialogTag)?.isAdded ?: false
            if (!isDialogShown) datePickerDialog.show(parentFragmentManager, DatePickerDialogTag)
        }

        binding.llTime.setOnClickListener {
            val time = viewModel.reminderDateTime.value.toLocalTime()
            val timePickerDialogTheme = viewModel.folder.value.color.toTimePickerDialogTheme()
            val timePickerDialog =
                createTimePickerDialog(time.hour, time.minute, timeFormat, timePickerDialogTheme)
            val isDialogShown =
                parentFragmentManager.findFragmentByTag(TimePickerDialogTag)?.isAdded ?: false
            if (!isDialogShown) timePickerDialog.show(parentFragmentManager, TimePickerDialogTag)
        }

//        }.launchIn(lifecycleScope)
    }

    private fun setupFolder(folder: Folder) {
        context?.let { context ->
            val color = context.resources.getColor(folder.color.toColorResourceId())
            val colorStateList = color.toColorStateList()
            binding.btnSet.background?.mutate()?.setTint(color)
            binding.llDate.background?.setRippleColor(colorStateList)
            binding.llTime.background?.setRippleColor(colorStateList)
        }
    }

    private fun setupNote(note: NoteEntity) {
        context?.let { context ->
            if (note.reminderDate == null) {
                binding.btnCancel.isVisible = false
                binding.btnSet.text = context.resources.getString(R.string.set_reminder)
            } else {
                binding.btnCancel.isVisible = true
                binding.btnSet.text = context.resources.getString(R.string.update_reminder)
            }
        }
    }

    private fun createTimePickerDialog(
        hour: Int,
        minute: Int,
        @TimeFormat timeFormat: Int,
        @StyleRes themeResId: Int
    ): MaterialTimePicker {
        return MaterialTimePicker.Builder()
            .setTimeFormat(timeFormat)
            .setTitleText(context?.getString(R.string.reminder_time))
            .setTheme(themeResId)
            .setHour(hour)
            .setMinute(minute)
            .build()
            .apply {
                addOnPositiveButtonClickListener {
                    Log.d("TAG", "createTimePickerDialog: $hour $minute")
                    viewModel.setReminderTime(this.hour, this.minute)
                }
            }
    }

    private fun MyColor.toDatePickerDialogTheme() = when (this) {
        MyColor.Gray -> R.style.DatePickerDialog_Gray
        MyColor.Blue -> R.style.DatePickerDialog_Blue
        MyColor.Pink -> R.style.DatePickerDialog_Pink
        MyColor.Cyan -> R.style.DatePickerDialog_Cyan
        MyColor.Purple -> R.style.DatePickerDialog_Purple
        MyColor.Red -> R.style.DatePickerDialog_Red
        MyColor.Yellow -> R.style.DatePickerDialog_Yellow
        MyColor.Orange -> R.style.DatePickerDialog_Orange
        MyColor.Green -> R.style.DatePickerDialog_Green
        MyColor.Brown -> R.style.DatePickerDialog_Brown
        MyColor.BlueGray -> R.style.DatePickerDialog_BlueGray
        MyColor.Teal -> R.style.DatePickerDialog_Teal
        MyColor.Indigo -> R.style.DatePickerDialog_Indigo
        MyColor.DeepPurple -> R.style.DatePickerDialog_DeepPurple
        MyColor.DeepOrange -> R.style.DatePickerDialog_DeepOrange
        MyColor.DeepGreen -> R.style.DatePickerDialog_DeepGreen
        MyColor.LightBlue -> R.style.DatePickerDialog_LightBlue
        MyColor.LightGreen -> R.style.DatePickerDialog_LightGreen
        MyColor.LightRed -> R.style.DatePickerDialog_LightRed
        MyColor.LightPink -> R.style.DatePickerDialog_LightPink
        MyColor.Black -> R.style.DatePickerDialog
    }

    private fun MyColor.toTimePickerDialogTheme() = when (this) {
        MyColor.Gray -> R.style.TimePickerDialog_Gray
        MyColor.Blue -> R.style.TimePickerDialog_Blue
        MyColor.Pink -> R.style.TimePickerDialog_Pink
        MyColor.Cyan -> R.style.TimePickerDialog_Cyan
        MyColor.Purple -> R.style.TimePickerDialog_Purple
        MyColor.Red -> R.style.TimePickerDialog_Red
        MyColor.Yellow -> R.style.TimePickerDialog_Yellow
        MyColor.Orange -> R.style.TimePickerDialog_Orange
        MyColor.Green -> R.style.TimePickerDialog_Green
        MyColor.Brown -> R.style.TimePickerDialog_Brown
        MyColor.BlueGray -> R.style.TimePickerDialog_BlueGray
        MyColor.Teal -> R.style.TimePickerDialog_Teal
        MyColor.Indigo -> R.style.TimePickerDialog_Indigo
        MyColor.DeepPurple -> R.style.TimePickerDialog_DeepPurple
        MyColor.DeepOrange -> R.style.TimePickerDialog_DeepOrange
        MyColor.DeepGreen -> R.style.TimePickerDialog_DeepGreen
        MyColor.LightBlue -> R.style.TimePickerDialog_LightBlue
        MyColor.LightGreen -> R.style.TimePickerDialog_LightGreen
        MyColor.LightRed -> R.style.TimePickerDialog_LightRed
        MyColor.LightPink -> R.style.TimePickerDialog_LightPink
        MyColor.Black -> R.style.TimePickerDialog
    }
}