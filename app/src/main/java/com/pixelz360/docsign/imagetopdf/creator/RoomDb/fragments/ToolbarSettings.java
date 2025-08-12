package com.pixelz360.docsign.imagetopdf.creator.RoomDb.fragments;

public interface ToolbarSettings {
    void setupToolbar(ToolbarController toolbarController);

    // Get the x and y position after the button is draw on screen
// (It's important to note that we can't get the position in the onCreate(),
// because at that stage most probably the view isn't drawn yet, so it will return (0, 0))
}

