import xmlrpc.client
from odoo_config.models import OdooConfig


def validate_config():
    """Return (ok: bool, message: str|None)."""
    config = OdooConfig.objects.first()
    if not config:
        return False, "Configuration Odoo introuvable."
    try:
        common = xmlrpc.client.ServerProxy(f"{config.url}/xmlrpc/2/common")
        uid = common.authenticate(config.db, config.username, config.password, {})
        if not uid:
            return False, "Authentification au serveur Odoo échouée (db/username/password invalides)."
        return True, None
    except Exception as e:
        return False, f"Erreur de connexion à Odoo: {e}"


def get_skills():
    config = OdooConfig.objects.first()
    try:
        common = xmlrpc.client.ServerProxy(f"{config.url}/xmlrpc/2/common")
        uid = common.authenticate(config.db, config.username, config.password, {})
    except Exception:
        return []

    if not uid:
        return []

    models = xmlrpc.client.ServerProxy(f"{config.url}/xmlrpc/2/object")

    try:
        skills = models.execute_kw(
            config.db,
            uid,
            config.password,
            'hr.skill',
            'search_read',
            [[]],
            {'fields': ['id', 'name']}
        )
    except Exception:
        return []

    return skills

def get_feedbacks_for_candidate(skill_name=None, min_average=None, application_id=None):
    """Return feedbacks filtered by application_id (required).
    skill_name may be either a skill name or an id (string or int)."""
    config = OdooConfig.objects.first()
    if not config:
        return []

    try:
        common = xmlrpc.client.ServerProxy(f"{config.url}/xmlrpc/2/common")
        uid = common.authenticate(config.db, config.username, config.password, {})
    except Exception:
        return []

    if not uid:
        return []

    models = xmlrpc.client.ServerProxy(
        f"{config.url}/xmlrpc/2/object"
    )

    if application_id is None:
        return []
    domain = [('application_id', '=', application_id)]

    if skill_name is not None:
        skill_id_int = None
        skill_id_int = int(skill_name)
        

        skill_id_int
        q_domain = [[('skill_id', '=', skill_id_int), ('feedback_id.application_id', '=', application_id)]]
        try:
            question_recs = models.execute_kw(
                config.db,
                uid,
                config.password,
                'hr.feedback.question',
                'search_read',
                q_domain,
                {'fields': ['feedback_id']}
            )
        except Exception:
            return []

        feedback_ids = list({q['feedback_id'][0] for q in question_recs if q.get('feedback_id')})
        if not feedback_ids:
            return []
        domain.append(('id', 'in', feedback_ids))

    try:
        feedbacks = models.execute_kw(
            config.db,
            uid,
            config.password,
            'hr.feedback',
            'search_read',
            [domain],
            {
                'fields': [
                    'author_id',
                    'description',
                    'average_score',
                    'questions_count'
                ]
            }
        )
    except Exception:
        return []

    if min_average is not None:
        try:
            min_val = float(min_average)
            feedbacks = [
                f for f in feedbacks
                if f.get('average_score') is not None and float(f.get('average_score')) >= min_val
            ]
        except (TypeError, ValueError):
            pass

    # --- AJOUT : récupérer les questions pour chaque feedback ---
    for feedback in feedbacks:
        
        feedback['author_name'] = (
            feedback['author_id'][1] if feedback.get('author_id') else None
        )
        questions = models.execute_kw(
            config.db,
            uid,
            config.password,
            'hr.feedback.question',
            'search_read',
            [[('feedback_id', '=', feedback['id'])]],
            {
                'fields': ['label', 'answer', 'score']
            }
        )
        feedback['questions'] = questions

    return feedbacks
