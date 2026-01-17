from django.urls import path
from . import views


app_name = 'feedbacks'

urlpatterns = [
    path('', views.feedback_list, name='feedback_list'),
    path('link/', views.link_token, name='link_token'),
]