cmake_minimum_required(VERSION 3.18.1)

# Enable 16 KB alignment for compatibility with 16 KB page-size devices
set(CMAKE_SHARED_LINKER_FLAGS "${CMAKE_SHARED_LINKER_FLAGS} -Wl,-z,max-page-size=0x4000")

# Define the native library (shared)
add_library(
        native-lib
        SHARED
        src/main/cpp/native-lib.c)

# Find the Android NDK log library
find_library(
        log-lib
        log)

# Link libraries
target_link_libraries(
        native-lib
        ${log-lib}
        atomic
        m)  # Add atomic & math if you're using them (safe for most projects)

set_target_properties(native-lib PROPERTIES
        C_STANDARD 11
        C_STANDARD_REQUIRED ON
)