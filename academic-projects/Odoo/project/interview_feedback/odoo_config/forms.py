from django import forms
from .models import OdooConfig

class OdooConfigForm(forms.ModelForm):
    class Meta:
        model = OdooConfig
        fields = '__all__'
        widgets = {
            'password': forms.PasswordInput(render_value=True),
        }