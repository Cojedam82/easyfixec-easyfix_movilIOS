from django.conf.urls import url, include
from rest_framework import routers

from . import views
from . import viewsets


router = routers.DefaultRouter()
router.register(r'users', viewsets.UserViewSet)

urlpatterns = [
    url(r'^', include(router.urls)),
]
