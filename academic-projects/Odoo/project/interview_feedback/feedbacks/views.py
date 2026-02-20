from django.shortcuts import render, redirect
from django.contrib.auth.decorators import login_required
from django.contrib import messages
from .service.odoo_rpc import get_feedbacks_for_candidate, get_skills, validate_config
from .form import FeedbackFilterForm, TokenLinkForm
from .models import CandidateProfile
from odoo_config.models import OdooConfig
import xmlrpc.client


# Create your views here.

def feedback_list(request):
    feedbacks = []

    ok, message = validate_config()
    if ok:
        skills = get_skills()
    else:
        skills = []

    application_id = None
    profile = None
    if request.user.is_authenticated:
        profile = CandidateProfile.objects.filter(user=request.user).first()
        if profile and profile.odoo_applicant_id:
            application_id = profile.odoo_applicant_id

    form = FeedbackFilterForm(request.POST or None, skills=skills)

    if request.method == 'GET' and application_id:
        try:
            feedbacks = get_feedbacks_for_candidate(
                skill_name=None,
                min_average=None,
                application_id=application_id
            )
        except Exception:
            feedbacks = []

    if request.method == 'POST' and not application_id and request.user.is_authenticated:
        messages.error(request, "Veuillez lier votre compte à une candidature via le lien présent sur cette page avant d'effectuer une recherche.")
    elif form.is_valid():
        skill = form.cleaned_data['skill']
        min_average = form.cleaned_data['min_average']
        feedbacks = get_feedbacks_for_candidate(
            skill_name=skill,
            min_average=min_average,
            application_id=application_id
        )

    context_message = None if ok else message

    return render(request, 'feedbacks/feedbacks.html', {
        'form': form,
        'feedbacks': feedbacks,
        'message': context_message,
        'has_candidate_profile': bool(profile),
        'candidate_profile': profile,
    })


@login_required
def link_token(request):
    ok, msg = validate_config()
    if not ok:
        messages.error(request, "Configuration Odoo invalide : " + (msg or ""))
        return redirect('feedbacks:feedback_list')

    form = TokenLinkForm(request.POST or None)
    if request.method == 'POST' and form.is_valid():
        token = form.cleaned_data['token']
        try:
            config = OdooConfig.objects.first()
            common = xmlrpc.client.ServerProxy(f"{config.url}/xmlrpc/2/common")
            uid = common.authenticate(config.db, config.username, config.password, {})
            if not uid:
                messages.error(request, "Impossible de s'authentifier auprès d'Odoo pour valider le token.")
                return redirect('feedbacks:link_token')
            models = xmlrpc.client.ServerProxy(f"{config.url}/xmlrpc/2/object")
            apps = models.execute_kw(config.db, uid, config.password, 'hr.applicant', 'search_read', [[('access_token', '=', token)]], {'fields': ['id', 'partner_id']})
        except Exception:
            messages.error(request, "Erreur lors de la vérification du token auprès d'Odoo.")
            return redirect('feedbacks:link_token')

        if not apps:
            messages.error(request, "Token invalide.")
            return redirect('feedbacks:link_token')

        app_id = apps[0]['id']
        profile, _ = CandidateProfile.objects.get_or_create(user=request.user)
        profile.odoo_applicant_id = app_id
        profile.save()
        messages.success(request, "Compte lié avec succès à l'application Odoo.")
        return redirect('feedbacks:feedback_list')

    return render(request, 'feedbacks/link_token.html', {'form': form})

