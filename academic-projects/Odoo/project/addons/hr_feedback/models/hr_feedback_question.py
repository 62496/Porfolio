from odoo import models, fields, api
from odoo.exceptions import ValidationError

class HrFeedbackQuestion(models.Model):
    _name = 'hr.feedback.question'
    _description = 'Feedback Question'

    label = fields.Char(string="Question", required=True)
    answer = fields.Text(string="Answer")
    score = fields.Float(string="Score")

    feedback_id = fields.Many2one(
        'hr.feedback',
        required=True,
        ondelete='cascade'
    )

    skill_id = fields.Many2one(
        'hr.skill',
        string="Skill"
    )
    MIN_SCORE = 0
    MAX_SCORE = 5

    @api.constrains('score')
    def _check_score_beetween(self):
        for rec in self:
            if rec.score <self.MIN_SCORE or rec.score > self.MAX_SCORE:
                raise ValidationError(
                    f"Le score doit être compris entre {self.MIN_SCORE} et {self.MAX_SCORE}.")

    @api.onchange('score')
    def _onchange_score(self):
        if self.score and (self.score < 0 or self.score > 5):
            return {
                'warning': {
                    'title': 'Score invalide',
                    'message': f"Le score doit être compris entre {self.MIN_SCORE} et {self.MAX_SCORE}."
                }
            }