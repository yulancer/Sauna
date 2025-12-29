    @UiThread
    public void onResetDelayClick(View v) {
        StartSaunaDialog dialog = getStartSaunaDialog();
        if (dialog != null) {
            dialog.mRequiredToReadySeconds = 0;
            LocalTime noDelayReadyTime = dialog.mDialogStartTime.plusSeconds((int) dialog.mMaxHeatingSeconds);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                dialog.mTpReadyTime.setHour(noDelayReadyTime.getHourOfDay());
                dialog.mTpReadyTime.setMinute(noDelayReadyTime.getMinuteOfHour());
            } else {
                dialog.mTpReadyTime.setCurrentHour(noDelayReadyTime.getHourOfDay());
                dialog.mTpReadyTime.setCurrentMinute(noDelayReadyTime.getMinuteOfHour());
            }
        }
    }
    // Intentionally empty - dialog reference not needed