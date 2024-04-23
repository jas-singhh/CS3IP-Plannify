<div align="center">

  <img src="/logo.png" alt="logo" width="200" height="auto" />
  <h1>Plannify</h1>
  
  <p>
    A mobile app for planning meals to relieve stress!
  </p>
  
</div>

<br />

<!-- Table of Contents -->
# :notebook_with_decorative_cover: Table of Contents

- [About the Project](#star2-about-the-project)
  * [Screenshots](#camera-screenshots)
  * [Tech Stack](#space_invader-tech-stack)
  * [Features](#dart-features)
  * [Color Reference](#art-color-reference)
  * [Environment Variables](#key-environment-variables)
- [Getting Started](#toolbox-getting-started)
  * [Prerequisites](#bangbang-prerequisites)
  * [Installation](#gear-installation)
  * [Running Tests](#test_tube-running-tests)
  * [Run Locally](#running-run-locally)
  * [Deployment](#triangular_flag_on_post-deployment)
- [Usage](#eyes-usage)
- [Roadmap](#compass-roadmap)
- [Contributing](#wave-contributing)
  * [Code of Conduct](#scroll-code-of-conduct)
- [FAQ](#grey_question-faq)
- [License](#warning-license)
- [Contact](#handshake-contact)
- [Acknowledgements](#gem-acknowledgements)

  

<!-- About the Project -->
## :star2: About the Project


<!-- Screenshots -->
### :camera: Screenshots

<div align="center"> 
  <img src="screenshots/Plannify Homepage.png" alt="Plannify homepage" width="142px" heigh="auto"/>
    <img src="screenshots/Plannify Find Meals.png" alt="Plannify find meals page" width="142px" heigh="auto" />
    <img src="screenshots/Plannify Create Custom Meal.png" alt="Plannify create custom meal page" width="142px" heigh="auto" />
    <img src="screenshots/Plannify My Meals.png" alt="Plannify my meals page" width="142px" heigh="auto" />
    <img src="screenshots/Plannify Grocery List.png" alt="Plannify grocery list page" width="142px" heigh="auto" />
    <img src="screenshots/Plannify Settings.png" alt="Plannify settings page" width="142px" heigh="auto" />
</div>


<!-- TechStack -->
### :space_invader: Tech Stack

  <summary>
    <b>Client</b>
  <ul>
    <li><a href="https://developer.android.com/studio">Android Studio</a></li>
    <li><a href="https://www.java.com/en/">Java</a></li>
    <li><a href="https://developer.android.com/develop/ui/views/layout/declaring-layout">XML</a></li>
  </ul>
  </summary>

  <summary>
    <b>Server</b>
  <ul>
    <li>Third party libraries like <a href="https://square.github.io/retrofit/">Retrofit</a>, <a href=https://square.github.io/picasso">Picasso</a>, and <a href="https://github.com/xabaras/RecyclerViewSwipeDecorator">SwipeDecorator</a></li>
    <li><a href="https://spoonacular.com/food-api">Spoonacular API</a></li>
    <li><a href="https://paperquotes.com/">PaperQuotes API</a></li>
  </ul>
</summary>

<summary>
  <b>Database</b>
  <ul>
    <li><a href="https://developer.android.com/jetpack/androidx/releases/room">Room</a></li>
  </ul>
</summary>

<!-- Features -->
### :dart: Features

- <b>Meal Planning: </b>Plan your weekly meals.
- <b>Automated Grocery List: </b>Automatically generate your grocery list based on your planned meals.
- <b>Meal Suggestions: </b>Explore a range of suggested meals for different times of the day.
- <b>Find Meals: </b>Browse through specific meals from an extensive list of recipes based on your preferences.
- <b>Meal Health Rating: </b>View each meal's healthiness level through the intuitive star-rating system, encouraging healthier meal choices.
- <b>Motivational Quote Of The Day: </b>View motivational quotes updated each day to uplift your mood, encouraging you to adopt a healthier lifestyle.
- <b>Custom Meals: </b>Create your own meals for your weekly plan.
- <b>Animated Loading Screen: </b>Enjoy your experience with an animated loading screen.

<!-- Color Reference -->
### :art: Color Reference

| Color             | Hex                                                                |
| ----------------- | ------------------------------------------------------------------ |
| Primary Color | ![#121212](https://via.placeholder.com/10/121212?text=+) #121212 |
| Secondary Color | ![#8cc6f4](https://via.placeholder.com/10/8cc6f4?text=+) #8cc6f4 |
| Accent Color | ![#B85500](https://via.placeholder.com/10/B85500?text=+) #B85500 |


<!-- Getting Started -->
## 	:toolbox: Getting Started

<!-- Prerequisites -->
### :bangbang: Prerequisites

<li><a href="https://developer.android.com/studio">Android Studio</a></li>
<li>Emulator or physical Android device is required for running the application</li>

### 💻: Installation

<li>Clone the project</li>

```bash
  git clone https://github.com/jas-singhh/Final-Year-Project.git
```

<li>Open Android Studio and select "Open an existing Android Studio project"</li>
<li>Navigate to the directory where you cloned the repository and select the "Final-Year-Project-master" file.</li>
<li>The project will build and once it is built, you will be able to run it normally.</li>

<!-- FAQ -->
## :grey_question: FAQ

- I cannot view the grocery list when I navigate to My Meals page?

  + The grocery list is only displayed once you have some meals saved in your plan.

- Why can I not turn on notifications?

  + Enabling notifications requires specific permissions and if you do not grant them, the application will not allow you to enable notifications. To grant the permissions, you can navigate to the application's settings and enable the notifications' permissions.


<!-- License -->
## :warning: License

Distributed under the no License.


<!-- Contact -->
## :handshake: Contact

Jaskaran Singh - 200117797@aston.ac.uk

Project Link: https://github.com/jas-singhh/Final-Year-Project

<!-- Acknowledgments -->
## :gem: Acknowledgements

Use this section to mention useful resources and libraries that you have used in your projects.

 - [Retrofit](https://square.github.io/retrofit/): A powerful library that provides efficient ways of making API calls.
 - [Picasso](https://github.com/square/picasso): An effective image downloading and caching library for Android.
 - [SwipeDecorator](https://github.com/xabaras/RecyclerViewSwipeDecorator): A simple utility class to decorate the swiping functionality by adding colours and backgrounds.
 - [Android Studio Documentation](https://developer.android.com/develop): The comprehensive documentation was imperative for understanding and implementing functionality.
