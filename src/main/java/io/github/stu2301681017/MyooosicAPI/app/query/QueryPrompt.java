package io.github.stu2301681017.MyooosicAPI.app.query;

import io.github.stu2301681017.MyooosicAPI.core.ApiConstraints;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.Default;

public record QueryPrompt(
        @NotBlank(groups = {ApiConstraints.class, Default.class})
        @Size(max = 256, groups = {ApiConstraints.class, Default.class}) String query
) {
}
