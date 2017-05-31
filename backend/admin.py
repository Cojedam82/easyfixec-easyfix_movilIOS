from django.contrib import admin
from django.contrib.auth import admin as auth_admin
from django.contrib.auth.models import User, Group

from django.utils.translation import gettext_lazy as _
from django.contrib.auth.forms import AdminPasswordChangeForm
from .models import *

admin.site.unregister(Group)
admin.site.unregister(User)

class CategoryFilter(admin.SimpleListFilter):
    title = _('Category')
    parameter_name = 'category'

    def lookups(self, request, model_admin):
        categories = set([obj.category for obj in model_admin.model.objects.all()])
        return [(c.id, c.name) for c in categories]

    def queryset(self, request, queryset):
        if self.value():
            return queryset.filter(category__id__exact=self.value())
        else:
            return queryset


class ClientFilter(admin.SimpleListFilter):
    title = _('Client')
    parameter_name = 'client'

    def lookups(self, request, model_admin):
        clients = set([obj.client for obj in model_admin.model.objects.all()])
        return [(c.id, c.email) for c in clients]

    def queryset(self, request, queryset):
        if self.value():
            return queryset.filter(client__id__exact=self.value())
        else:
            return queryset


class ProviderFilter(admin.SimpleListFilter):
    title = _('Provider')
    parameter_name = 'provider'

    def lookups(self, request, model_admin):
        providers = set([e.provider for e in model_admin.model.objects.all()])
        return [(e.id, e.email) for e in providers]

    def queryset(self, request, queryset):
        if self.value():
            return queryset.filter(provider__id__exact=self.value())
        else:
            return queryset


class ProfileInline(admin.StackedInline):
    model = Profile
    can_delete = False
    verbose_name_plural = 'Profile'
    fk_name = 'user'


class ProviderServiceInline(admin.StackedInline):
    model = ProviderService


@admin.register(User)
class UserAdmin(auth_admin.UserAdmin):
    inlines = (ProfileInline, ProviderServiceInline)
    add_fieldsets = (
        (None, {
            'classes': ('wide',),
            'fields': ('email', 'password1', 'password2')}
        ),
    )
    fieldsets = (
        (None, {'fields': ('email', 'password',)}),
        (_('Personal info'), {'fields': ('first_name', 'last_name',
                                        )}),
        (_('Permissions'), {'fields': ('is_active', 'is_staff',
                                        'is_superuser')}),
        (_('Important dates'), {'fields': ('last_login', 'date_joined')}),
    )
    list_display = ('email', 'first_name', 'last_name', 'role', 'is_staff')
    list_filter = ('is_staff', 'is_superuser', 'is_active', 'profile__role')
    search_fields = ('email', 'first_name', 'last_name', 'profile__role')
    exclude_fields = ('groups', 'user_permissions')
    ordering = ('email',)

    def phone(self, instance):
        return instance.profile.phone
    phone.short_description = 'Teléfono'

    def role(self, instance):
        profile_role = dict(Profile.ROLE_CHOICES).get(instance.profile.role)
        return profile_role if profile_role else 'Administrador'
    role.short_description = 'Rol'

    def get_inline_instances(self, request, obj=None):
        if not obj:
            return list()
        return super(UserAdmin, self).get_inline_instances(request, obj)


@admin.register(Category)
class CategoryAdmin(admin.ModelAdmin):
    list_display = ('name', 'date_created', 'is_active')
    list_filter = ('name', 'is_active')


@admin.register(Service)
class ServiceAdmin(admin.ModelAdmin):
    list_display = ('name', 'category', 'date_created', 'is_active')
    list_filter = (CategoryFilter, 'name', 'is_active')


@admin.register(Location)
class LocationAdmin(admin.ModelAdmin):
    list_display = ('provider', 'latitude', 'longitude', 'date_created')
    list_filter = (ProviderFilter,)

    def get_form(self, request, obj=None, **kwargs):
        form = super(LocationAdmin, self).get_form(request, obj, **kwargs)
        form.base_fields['provider'].queryset = User.objects.filter(profile__role=2)
        return form

@admin.register(Reservation)
class ReservationAdmin(admin.ModelAdmin):
    list_display = ('client', 'provider', 'time', 'date', 'status')
    list_filter = (ClientFilter, ProviderFilter, 'status')

    def get_form(self, request, obj=None, **kwargs):
        form = super(ReservationAdmin, self).get_form(request, obj, **kwargs)
        form.base_fields['client'].queryset = User.objects.filter(profile__role=1)
        form.base_fields['provider'].queryset = User.objects.filter(profile__role=2)
        return form


@admin.register(Evaluation)
class EvaluationAdmin(admin.ModelAdmin):
    list_display = ('provider', 'score', 'date_created')
    list_filter = (ProviderFilter,)

    def get_form(self, request, obj=None, **kwargs):
        form = super(EvaluationAdmin, self).get_form(request, obj, **kwargs)
        form.base_fields['provider'].queryset = User.objects.filter(profile__role=2)
        return form
