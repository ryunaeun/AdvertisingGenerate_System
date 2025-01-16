import { createRouter, createWebHistory } from "vue-router";
import LandingPage from "../views/MainPages/LandingPage/index.vue";
import HomePage from "../views/MainPages/HomePage/index.vue";
import GalleryPage from "../views/MainPages/GalleryPage/index.vue";
import BoardPage from "../views/MainPages/BoardPage/index.vue";
import PricingPage from "../views/MainPages/PricingPage/index.vue";
import RegisterPage from "../views/MainPages/RegisterPage/index.vue";
import LoginPage from "../views/MainPages/LoginPage/index.vue";
import AdminPage from "../views/MainPages/AdminPage/index.vue";
import PresentationView from "../views/Presentation/PresentationView.vue";
import AboutView from "../views/LandingPages/AboutUs/AboutView.vue";
import ContactView from "../views/LandingPages/ContactUs/ContactView.vue";
import AuthorView from "../views/LandingPages/Author/AuthorView.vue";
import SignInBasicView from "../views/LandingPages/SignIn/BasicView.vue";
import PageHeaders from "../layouts/sections/page-sections/page-headers/HeadersView.vue";
import PageFeatures from "../layouts/sections/page-sections/features/FeaturesView.vue";
import NavigationNavbars from "../layouts/sections/navigation/navbars/NavbarsView.vue";
import NavigationNavTabs from "../layouts/sections/navigation/nav-tabs/NavTabsView.vue";
import NavigationPagination from "../layouts/sections/navigation/pagination/PaginationView.vue";
import InputAreasInputs from "../layouts/sections/input-areas/inputs/InputsView.vue";
import InputAreasForms from "../layouts/sections/input-areas/forms/FormsView.vue";
import ACAlerts from "../layouts/sections/attention-catchers/alerts/AlertsView.vue";
import ACModals from "../layouts/sections/attention-catchers/modals/ModalsView.vue";
import ACTooltipsPopovers from "../layouts/sections/attention-catchers/tooltips-popovers/TooltipsPopoversView.vue";
import ElAvatars from "../layouts/sections/elements/avatars/AvatarsView.vue";
import ElBadges from "../layouts/sections/elements/badges/BadgesView.vue";
import ElBreadcrumbs from "../layouts/sections/elements/breadcrumbs/BreadcrumbsView.vue";
import ElButtons from "../layouts/sections/elements/buttons/ButtonsView.vue";
import ElButtonGroups from "../layouts/sections/elements/button-groups/ButtonGroupsView.vue";
import ElDropdowns from "../layouts/sections/elements/dropdowns/DropdownsView.vue";
import ElProgressBars from "../layouts/sections/elements/progress-bars/ProgressBarsView.vue";
import ElToggles from "../layouts/sections/elements/toggles/TogglesView.vue";
import ElTypography from "../layouts/sections/elements/typography/TypographyView.vue";

import axios from "axios"
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "landing",
      component: LandingPage,
    },
    {
      path: "/home",
      name: "home",
      component: HomePage,
      //meta: { requiresAuth: true }, // 인증이 필요한 경로 표시 
    },
    {
      path: "/gallery",
      name: "gallery",
      component: GalleryPage
    },
    {
      path: "/pricing",
      name: "pricing",
      component: PricingPage
    },
    {
      path: "/board",
      name: "board",
      component: BoardPage
    },
    {
      path: "/login",
      name: "login",
      component: LoginPage
    },
    {
      path: "/register",
      name: "register",
      component: RegisterPage
    },
    {
      path: "/admin",
      name: "admin",
      component: AdminPage,
      //meta: { requiresAuth: true, requiresRole: "ROLE_ADMIN" }, // 관리자 권한 필요
    },
    {
      path: "/presentation",
      name: "presentation",
      component: PresentationView,
    },
    {
      path: "/examples/landing-pages/about-us",
      name: "about",
      component: AboutView,
    },
    {
      path: "/examples/landing-pages/contact-us",
      name: "contactus",
      component: ContactView,
    },
    {
      path: "/examples/landing-pages/author",
      name: "author",
      component: AuthorView,
    },
    {
      path: "/examples/landing-pages/basic",
      name: "signin-basic",
      component: SignInBasicView,
    },
    {
      path: "/examples/page-sections/page-headers",
      name: "page-headers",
      component: PageHeaders,
    },
    {
      path: "/examples/page-sections/features",
      name: "page-features",
      component: PageFeatures,
    },
    {
      path: "/examples/navigation/navbars",
      name: "navigation-navbars",
      component: NavigationNavbars,
    },
    {
      path: "/examples/navigation/nav-tabs",
      name: "navigation-navtabs",
      component: NavigationNavTabs,
    },
    {
      path: "/examples/navigation/pagination",
      name: "navigation-pagination",
      component: NavigationPagination,
    },
    {
      path: "/examples/input-areas/inputs",
      name: "inputareas-inputs",
      component: InputAreasInputs,
    },
    {
      path: "/examples/input-areas/forms",
      name: "inputareas-forms",
      component: InputAreasForms,
    },
    {
      path: "/examples/attention-catchers/alerts",
      name: "ac-alerts",
      component: ACAlerts,
    },
    {
      path: "/examples/attention-catchers/modals",
      name: "ac-modals",
      component: ACModals,
    },
    {
      path: "/examples/attention-catchers/tooltips-popovers",
      name: "ac-tooltips-popovers",
      component: ACTooltipsPopovers,
    },
    {
      path: "/examples/elements/avatars",
      name: "el-avatars",
      component: ElAvatars,
    },
    {
      path: "/examples/elements/badges",
      name: "el-badges",
      component: ElBadges,
    },
    {
      path: "/examples/elements/breadcrumbs",
      name: "el-breadcrumbs",
      component: ElBreadcrumbs,
    },
    {
      path: "/examples/elements/buttons",
      name: "el-buttons",
      component: ElButtons,
    },
    {
      path: "/examples/elements/button-groups",
      name: "el-button-groups",
      component: ElButtonGroups,
    },
    {
      path: "/examples/elements/dropdowns",
      name: "el-dropdowns",
      component: ElDropdowns,
    },
    {
      path: "/examples/elements/progress-bars",
      name: "el-progress-bars",
      component: ElProgressBars,
    },
    {
      path: "/examples/elements/toggles",
      name: "el-toggles",
      component: ElToggles,
    },
    {
      path: "/examples/elements/typography",
      name: "el-typography",
      component: ElTypography,
    },
  ],
});


// router.beforeEach(async (to, from, next) => {
//   const accessToken = sessionStorage.getItem("accessToken");
//   const refreshToken = localStorage.getItem("refreshToken");

//   if (to.matched.some((record) => record.meta.requiresAuth)) {
//     if (!accessToken && refreshToken) {
//       try {
//         const response = await axios.post("http://localhost:8080/api/refresh-token", { refreshToken });
//         const newAccessToken = response.data.accessToken;

//         sessionStorage.setItem("accessToken", newAccessToken);
//         console.log("Access Token 갱신 성공");

//         const userResponse = await axios.get("http://localhost:8080/api/current-user", {
//           headers: { Authorization: `Bearer ${newAccessToken}` },
//         });
//         const userRole = userResponse.data.role;

//         if (userRole === "ROLE_ADMIN" && to.name !== "admin") {
//           return next({ name: "admin" });
//         } else if (userRole !== "ROLE_ADMIN" && to.name !== "home") {
//           return next({ name: "home" });
//         }

//         return next();
//       } catch (error) {
//         console.error("Access Token 갱신 실패:", error);
//         localStorage.removeItem("refreshToken");
//         sessionStorage.removeItem("accessToken");
//         alert("세션이 만료되었습니다. 다시 로그인해주세요.");
//         return next({ name: "loginPage" });
//       }
//     } else if (!accessToken) {
//       console.warn("Refresh Token 없음, 로그인 페이지로 이동");
//       return next({ name: "loginPage" });
//     }
//   }

//   next();
// });


export default router;
