from django.db import models
from django.db.models.signals import post_save, pre_save
from django.dispatch import receiver
from django.contrib.auth.models import User
from django.core.validators import MaxValueValidator, MinValueValidator
from django.utils.translation import gettext_lazy as _
from django.utils import timezone

from rest_framework.authtoken.models import Token
from phonenumber_field.modelfields import PhoneNumberField


class Profile(models.Model):
    ROLE_CHOICES = (
        (1, 'Cliente'),
        (2, 'Proveedor'),
    )
    user = models.OneToOneField(User)
    phone = PhoneNumberField(
        _('Phone'),
        blank=True,
    )
    role = models.PositiveSmallIntegerField(
        _('Role'),
        choices=ROLE_CHOICES,
        default=1,
        blank=True,
        null=True,
    )

    def get_token(self):
        return self.user.auth_token.key
    token = property(get_token)

    class Meta:
        default_related_name = 'profile'
        verbose_name = _('Profile')
        verbose_name_plural = _('Profile')

    def __str__(self):
        return self.user.get_full_name()


class BaseModel(models.Model):
    name = models.CharField(
        _('Name'),
        max_length=255,
    )
    date_created = models.DateTimeField(
        _('date created'),
        auto_now=True,
    )
    is_active = models.BooleanField(
        _('Active'),
        default=True,
    )

    class Meta:
        abstract = True


class Category(BaseModel):
    class Meta:
        default_related_name = 'categories'
        verbose_name = _('Category')
        verbose_name_plural = _('Categories')

    def __str__(self):
        return self.name


class Service(BaseModel):
    category = models.ForeignKey(
        Category,
        on_delete=models.CASCADE,
        verbose_name=_('Category'),
    )
    image = models.ImageField(
        _('Image'),
        upload_to='services',
        blank = True,
        null = True)

    class Meta:
        default_related_name = 'services'
        verbose_name = _('Service')
        verbose_name_plural = _('Services')

    def __str__(self):
        return self.name


class ProviderService(models.Model):
    provider = models.ForeignKey(
        User,
        on_delete=models.CASCADE,
        verbose_name=_('Provider'),
    )
    service = models.ForeignKey(
        Service,
        on_delete=models.CASCADE,
        verbose_name=_('Service'),
    )

    class Meta:
        default_related_name = 'provider_services'
        verbose_name = _('Provider Service')
        verbose_name_plural = _('Provider Services')


class Location(models.Model):
    provider = models.ForeignKey(
        User,
        on_delete=models.CASCADE,
        verbose_name=_('Provider'),
    )
    longitude = models.FloatField(_('Longitude'))
    latitude = models.FloatField(_('Latitude'))
    date_created = models.DateTimeField(
        _('date created'),
        auto_now=True,
    )

    class Meta:
        default_related_name = 'locations'
        verbose_name = _('Locations')
        verbose_name_plural = _('Location')


class Reservation(models.Model):
    STATUS_CHOICES = (
        (1,'Asignada'),
        (2,'Cancelada'),
        (3,'Pendiente'),
        (4,'No Realizada'),
        (5,'Realizada'),
    )
    client = models.ForeignKey(
        User,
        on_delete=models.CASCADE,
        verbose_name=_('Client'),
        related_name='client_reservations',
    )
    provider = models.ForeignKey(
        User,
        on_delete=models.CASCADE,
        verbose_name=_('Provider'),
        related_name='provider_reservations',
        blank=True,
        null=True,
    )
    time = models.TimeField(_('Time'))
    date = models.DateField(_('Date'))
    status = models.PositiveIntegerField(
        _('Status'),
        choices=STATUS_CHOICES,
        default=3,
        blank=True,
    )

    class Meta:
        verbose_name = _('Reservation')
        verbose_name_plural = _('Reservations')


class Evaluation(models.Model):
    provider = models.ForeignKey(
        User,
        on_delete=models.CASCADE,
        verbose_name=_('Provider'),
    )
    score = models.PositiveIntegerField(
        _('Score'),
        validators=[MinValueValidator(0), MaxValueValidator(5)],
    )
    date_created = models.DateTimeField(
        _('date created'),
        auto_now=True,
    )

    class Meta:
        default_related_name = 'evaluations'
        verbose_name = _('Evaluation')
        verbose_name_plural = _('Evaluations')


@receiver(pre_save, sender=User)
def pre_user_save(sender, instance, **kwargs):
    if not instance.id:
        instance.username = instance.email


@receiver(post_save, sender=User)
def post_user_save(sender, instance, created=False, **kwargs):
    if created:
        Profile.objects.create(user=instance)
    instance.profile.save()


@receiver(post_save, sender=User)
def create_auth_token(sender, instance=None, created=False, **kwargs):
    if created:
        Token.objects.create(user=instance)
