from django import forms

class FeedbackFilterForm(forms.Form):
    skill = forms.ChoiceField(
        required=False,
        label="Compétence",
        choices=[]
    )
    min_average = forms.IntegerField(
        label="Score moyen minimum",
        required=False,
        min_value=0,
        max_value=5
    )

    def clean_skill(self):
        skill = self.cleaned_data.get("skill")
        if not skill:
            return None
        try:
            return int(skill)
        except (TypeError, ValueError):
            return None

    def __init__(self, *args, **kwargs):
        skills = kwargs.pop('skills', [])
        super().__init__(*args, **kwargs)

        # Use skill id as the choice value for robustness
        self.fields['skill'].choices = [('', '---')] + [
            (str(skill['id']), skill['name']) for skill in skills
        ]


class TokenLinkForm(forms.Form):
    token = forms.CharField(label="Token d'accès", max_length=256)