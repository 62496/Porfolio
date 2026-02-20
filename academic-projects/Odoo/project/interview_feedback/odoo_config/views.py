from django.shortcuts import render
import xmlrpc.client
from .forms import OdooConfigForm
from .models import OdooConfig
# Create your views here.


def config_view(request):
    config = OdooConfig.objects.first()
    form = OdooConfigForm(request.POST or None, instance=config)
    result = None

    if request.method == 'POST' and form.is_valid():
        config = form.save()
        try:
            common = xmlrpc.client.ServerProxy(
                f"{config.url}/xmlrpc/2/common"
            )
            uid = common.authenticate(
                config.db,
                config.username,
                config.password,
                {}
            )
            result = "Connexion réussie" if uid else "Connexion échouée"
        except Exception:
            result = "Connexion échouée"
    return render(request, 'odoo_config/index.html', {
        'form': form,
        'result': result
    })